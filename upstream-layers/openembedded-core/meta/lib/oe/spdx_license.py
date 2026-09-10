#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import re
import json
from abc import abstractmethod, ABC
from enum import Enum
from pathlib import Path
from dataclasses import dataclass

THIS_DIR = Path(__file__).parent

_LICENSES = {}
_EXCEPTIONS = {}


def _load_licenses():
    p = THIS_DIR.parent.parent / "files" / "spdx-licenses.json"
    with p.open("r") as f:
        data = json.load(f)

    for lic in data["licenses"]:
        _LICENSES[lic["licenseId"].lower()] = lic


_load_licenses()


def _load_exceptions():
    p = THIS_DIR.parent.parent / "files" / "spdx-exceptions.json"
    with p.open("r") as f:
        data = json.load(f)

    for lic in data["exceptions"]:
        _EXCEPTIONS[lic["licenseExceptionId"].lower()] = lic


_load_exceptions()


def get_license(ident):
    return _LICENSES.get(ident.lower())


def get_exception(ident):
    return _EXCEPTIONS.get(ident.lower())


class ParseError(Exception):
    def __init__(self, n, s):
        super().__init__(s)
        self.n = n
        self.expression = ""

    def format(self, *, prefix="ERROR:"):
        if not self.n:
            return f"{prefix}0: {str(self)}\n{self.expression}"

        start, end = self.n.get_range()

        if start < 0:
            start = 0

        if end < 0:
            end = len(self.expression)

        ident = " " * start
        for i in range(start, end):
            ident += "^"

        return "\n".join(
            [
                f"{prefix}{start}: " + str(self),
                self.expression,
                ident,
            ]
        )


def check_stack_types(stack, types):
    if len(stack) < len(types):
        return False

    for i, t in enumerate(types):
        if isinstance(t, list):
            t = tuple(t)
        if not isinstance(stack[-(len(types) - i)], t):
            return False

    return True


class Token(object):
    def __init__(self):
        self.value = ""
        self.start = 0
        self.end = 0

    def copy(self):
        t = self.__class__()
        t.value = self.value
        t.start = self.start
        t.end = self.end
        return t

    def get_range(self):
        return self.start, self.end


class Node(ABC):
    @dataclass
    class Sort:
        node: object
        key: list

    def __init__(self, children, *, token=None):
        self.token = token
        self.children = children

    @abstractmethod
    def copy(self):
        raise NotImplementedError("Method not implemented")

    @abstractmethod
    def to_string(self):
        raise NotImplementedError("Method not implemented")

    def _copy_token(self):
        if self.token:
            return self.token.copy()
        return None

    def iter(self):
        yield self

        for c in self.children:
            yield from c.iter()

    def get_range(self):
        if self.token:
            start = self.token.start
            end = self.token.end
        else:
            start = -1
            end = -1

        for c in self.children:
            child_start, child_end = c.get_range()
            if start < 0:
                start = child_start
            else:
                start = min(start, child_start)
            if end < 0:
                end = child_end
            else:
                end = max(end, child_end)

        return start, end

    def _simplify(self):
        self.children = [c._simplify() for c in self.children]
        return self

    def to_ast_string(self, indent=0):
        s = " " * indent + self.__class__.__name__
        if self.token:
            s += f"({self.token.value})"
        s += "\n"
        for c in self.children:
            s += c.to_ast_string(indent + 2)
        return s

    def sort(self):
        """
        Sort the tree into a consistent ordering and removing duplicate
        licenses (e.g. "FOO AND FOO" or "FOO OR FOO")

        Returns the root of a new tree which is a sorted copy of the original
        """
        return self.copy()._sort().node

    def _sort(self):
        raise NotImplementedError("Method not implemented")


class ReservedToken(Node):
    def __init__(self, **kwargs):
        super().__init__([], **kwargs)

    def copy(self):
        return self.__class__(token=self._copy_token())

    @property
    def value(self):
        return self.token.value

    @property
    def start(self):
        return self.token.start

    @property
    def end(self):
        return self.token.end

    def to_string(self):
        return self.token.value

    @classmethod
    def reduce(cls, stack, lookahead):
        if not check_stack_types(stack, [Token]):
            return False

        token = stack[-1]
        if not token.value in RESERVED:
            return False

        stack.pop()
        stack.append(cls(token=token))

    def _sort(self):
        return Node.Sort(self, [self.__class__.__name__, self.token.value])


class Identifier(Node):
    def __init__(self, ident, **kwargs):
        super().__init__([], **kwargs)
        self.ident = ident

    def copy(self):
        return self.__class__(self.ident, token=self._copy_token())

    def to_string(self):
        return self.ident

    def _sort(self):
        return Node.Sort(self, [self.__class__.__name__, self.ident])


class UnknownId(Identifier):
    @classmethod
    def reduce(cls, stack, lookahead):
        if not check_stack_types(stack, [Token]):
            return False

        t = stack.pop()
        stack.append(cls(t.value, token=t))
        return True


class LicenseId(Identifier):
    @property
    def deprecated(self):
        return get_license(self.ident)["isDeprecatedLicenseId"]

    @property
    def name(self):
        return self.ident

    @classmethod
    def reduce(cls, stack, lookahead):
        if not check_stack_types(stack, [Token]):
            return False

        lic = get_license(stack[-1].value)
        if lic is None:
            return False

        stack.append(cls(lic["licenseId"], token=stack.pop()))
        return True


class ExceptionId(Identifier):
    @property
    def deprecated(self):
        return get_exception(self.ident)["isDeprecatedLicenseId"]

    @property
    def name(self):
        return self.ident

    @classmethod
    def reduce(cls, stack, lookahead):
        if not check_stack_types(stack, [Token]):
            return False

        lic = get_exception(stack[-1].value)
        if lic is None:
            return False

        stack.append(cls(lic["licenseExceptionId"], token=stack.pop()))
        return True


class LicenseRef(Identifier):
    REGEX = re.compile(
        r"^(DocumentRef-[A-Za-z0-9\.\-]+:)?LicenseRef-(?P<name>[A-Za-z0-9\.\-]+)$"
    )

    def __init__(self, ident, name, **kwargs):
        super().__init__(ident, **kwargs)
        self.name = name

    def copy(self):
        return self.__class__(self.ident, self.name, token=self._copy_token())

    @classmethod
    def reduce(cls, stack, lookahead):
        if not check_stack_types(stack, [Token]):
            return False

        m = cls.REGEX.match(stack[-1].value)
        if m is None:
            return False

        t = stack.pop()
        stack.append(cls(t.value, m.group("name"), token=t))
        return True


class CompoundExpression(Node):
    def __init__(self, child, **kwargs):
        super().__init__([child], **kwargs)

    def copy(self):
        return self.child.copy()

    @property
    def child(self):
        return self.children[0]

    def to_string(self):
        return self.child.to_string()

    def _simplify(self):
        return self.child._simplify()

    @classmethod
    def reduce(cls, stack, lookahead):
        if check_stack_types(stack, [ReservedToken]) and stack[-1].value == ")":
            if not check_stack_types(
                stack,
                [
                    COMPOUND_EXPRESSION,
                    ReservedToken,
                ],
            ):
                raise ParseError(stack[-2], "Invalid expression for parentheses")

            if not check_stack_types(
                stack,
                [
                    ReservedToken,
                    COMPOUND_EXPRESSION,
                    ReservedToken,
                ],
            ):
                raise ParseError(stack[-1], "Missing matching '('")

            rparen = stack[-1]
            n = stack[-2]
            lparen = stack[-3]

            if lparen.value != "(":
                raise ParseError(lparen, "'(' expected, but not found")

            stack.pop()
            stack.pop()
            stack.pop()

            stack.append(cls(n))
            return True

        if not check_stack_types(
            stack,
            [(AndOp, OrOp, WithOp)],
        ):
            return False

        stack.append(cls(stack.pop()))
        return True

    def _sort(self):
        return self.child._sort()


SIMPLE_EXPRESSION = [LicenseId, LicenseRef, UnknownId]
COMPOUND_EXPRESSION = SIMPLE_EXPRESSION + [CompoundExpression]


class Operator(Node):
    OPERATORS = {}


class UnaryOp(Operator):
    def __init__(self, child, **kwargs):
        super().__init__([child], **kwargs)

    def __init_subclass__(cls):
        Operator.OPERATORS[cls.NAME] = cls

    def copy(self):
        return self.__class__(self.child.copy(), token=self._copy_token())

    @property
    def child(self):
        return self.children[0]

    def to_string(self):
        if isinstance(self.child, Operator) and self.child.PRECEDENCE > self.PRECEDENCE:
            return self.NAME + " (" + self.child.to_string() + ")"
        return self.NAME + " " + self.child.to_string()

    @classmethod
    def reduce(cls, stack, lookahead):
        if not check_stack_types(stack, cls.TYPES):
            return False

        child = stack[-1]
        token = stack[-2]

        if token.value != cls.NAME:
            return False

        if token.value not in cls.OPERATORS:
            return False

        if (
            lookahead
            and lookahead.value in cls.OPERATORS
            and cls.PRECEDENCE < cls.OPERATORS[lookahead.value].PRECEDENCE
        ):
            return False

        stack.pop()
        stack.pop()

        stack.append(cls(child, token=token))
        return True


class BinOp(Operator):
    def __init__(self, left, right, **kwargs):
        super().__init__([left, right], **kwargs)

    def __init_subclass__(cls):
        if name := getattr(cls, "NAME", None):
            Operator.OPERATORS[name] = cls

    def copy(self):
        return self.__class__(
            self.left.copy(), self.right.copy(), token=self._copy_token()
        )

    @property
    def left(self):
        return self.children[0]

    @property
    def right(self):
        return self.children[1]

    def to_string(self):
        strs = []
        for c in self.children:
            s = c.to_string()
            if isinstance(c, BinOp) and c.PRECEDENCE < self.PRECEDENCE:
                s = "(" + s + ")"
            if strs:
                strs.append(self.NAME)
            strs.append(s)

        return " ".join(strs)

    @classmethod
    def reduce(cls, stack, lookahead):
        if not check_stack_types(stack, cls.TYPES):
            return False

        right = stack[-1]
        token = stack[-2]
        left = stack[-3]

        if token.value != cls.NAME:
            return False

        if token.value not in cls.OPERATORS:
            return False

        if (
            lookahead
            and lookahead.value in cls.OPERATORS
            and cls.PRECEDENCE < cls.OPERATORS[lookahead.value].PRECEDENCE
        ):
            return False

        stack.pop()
        stack.pop()
        stack.pop()

        stack.append(cls(left, right, token=token))
        return True


class ConjunctionOp(BinOp):
    def __init__(self, left, right, **kwargs):
        super().__init__(left, right, **kwargs)

    @classmethod
    def join(cls, nodes):
        if len(nodes) == 0:
            return None

        if len(nodes) == 1:
            return nodes[0]

        root = cls(nodes.pop(), nodes.pop())
        while nodes:
            root = cls(root, nodes.pop())
        return root

    def _sort(self):
        # Sort children
        children = sorted([c._sort() for c in self.children], key=lambda x: x.key)
        self.children = [c.node for c in children]
        s = Node.Sort(self, [self.__class__.__name__] + [c.key for c in children])

        if children[0].key == children[1].key:
            # Duplicate expressions
            return children[0]

        if isinstance(self.left, Identifier) and isinstance(self.right, Identifier):
            if self.left.ident == self.right.ident:
                return (self.left, [self.left.ident])
            # Already sorted
            return s

        # Find the parent of the left most child of the right child
        right_parent = None
        if isinstance(self.right, self.__class__):
            right_parent = self.right
            while isinstance(right_parent.left, self.__class__):
                right_parent = right_parent.left

        # Find the parent of the right most child of the left child
        left_parent = None
        if isinstance(self.left, self.__class__):
            left_parent = self.left
            while isinstance(left_parent.right, self.__class__):
                left_parent = left_parent.right

        if left_parent and right_parent:
            left_key = left_parent.right._sort().key
            right_key = right_parent.left._sort().key
            if left_key == right_key:
                # Swap right children to allow the identical nodes to
                # eventually merge together
                tmp = self.children[1]
                self.children[1] = self.left.children[1]
                self.left.children[1] = tmp

                # Sort again
                return self._sort()

            if left_key > right_key:
                tmp = left_parent.children[1]
                left_parent.children[1] = right_parent.children[0]
                right_parent.children[0] = tmp

                # Sort again
                return self._sort()

            # We don't want a "balanced" node (that is, one with both
            # children being the same) to ensure that the resulting
            # tree is consistent (and thus, comparable). Swap terminal
            # nodes left to "bubble" up this node
            tmp = left_parent.children[1]
            left_parent.children[1] = self.children[1]
            self.children = [tmp, left_parent]
            return self._sort()

        elif left_parent:
            left_key = left_parent.right._sort().key
            right_key = self.right._sort().key
            if left_key == right_key:
                return children[0]

            if left_key > right_key:
                tmp = self.children[1]
                self.children[1] = left_parent.children[1]
                left_parent.children[1] = tmp

                # Sort again
                return self._sort()

        elif right_parent:
            left_key = self.left._sort().key
            right_key = right_parent.left._sort().key
            if left_key == right_key:
                return children[1][0], children[1][1]

            if left_key > right_key:
                tmp = self.children[0]
                self.children[0] = right_parent.children[0]
                right_parent.children[0] = tmp

                # Sort again
                return self._sort()

        return s


class OrOp(ConjunctionOp):
    NAME = "OR"
    PRECEDENCE = 1
    TYPES = [COMPOUND_EXPRESSION, ReservedToken, COMPOUND_EXPRESSION]


class AndOp(ConjunctionOp):
    NAME = "AND"
    PRECEDENCE = 2
    TYPES = [COMPOUND_EXPRESSION, ReservedToken, COMPOUND_EXPRESSION]


class NotOp(UnaryOp):
    NAME = "NOT"
    PRECEDENCE = 3
    TYPES = [ReservedToken, COMPOUND_EXPRESSION]


class WithOp(BinOp):
    NAME = "WITH"
    PRECEDENCE = 4
    TYPES = [SIMPLE_EXPRESSION, ReservedToken, ExceptionId]

    @property
    def license(self):
        return self.left

    @property
    def exception(self):
        return self.right

    def _sort(self):
        return Node.Sort(
            self,
            [
                self.__class__.__name__,
                self.license._sort().key,
                self.exception._sort().key,
            ],
        )


RESERVED = set(Operator.OPERATORS.keys()) | {"(", ")", "+"}

REDUCTIONS = (
    ReservedToken.reduce,
    LicenseId.reduce,
    ExceptionId.reduce,
    LicenseRef.reduce,
    WithOp.reduce,
    NotOp.reduce,
    AndOp.reduce,
    OrOp.reduce,
    CompoundExpression.reduce,
)


def _reduce(stack, lookahead):
    # print(stack)
    for r in REDUCTIONS:
        if r(stack, lookahead):
            # print(f"Applied reduction {r}")
            _reduce(stack, lookahead)


def tokenize(s):
    tokens = []
    t = Token()

    def end_token(end):
        nonlocal t
        nonlocal tokens
        if t.value:
            t.end = end + 1
            tokens.append(t)
        t = Token()

    for idx, c in enumerate(s):
        if c in RESERVED:
            end_token(idx - 1)
            t.start = idx
            t.value = c
            end_token(idx)
        elif c in (" ", "\t", "\n", "\r"):
            end_token(idx - 1)
        else:
            if not t.value:
                t.start = idx
            t.value += c

    end_token(len(s) - 1)
    return tokens


def create_ast(tokens, *, allow_unknown=False):
    stack = []
    while tokens:
        t = tokens.pop(0)

        if tokens:
            lookahead = tokens[0]
        else:
            lookahead = None

        stack.append(t)
        _reduce(stack, lookahead)
        # If unknown IDs are allowed, attempt a reduction to an unknown ID
        if allow_unknown and UnknownId.reduce(stack, lookahead):
            _reduce(stack, lookahead)

    if len(stack) == 0:
        raise ParseError(None, "Empty expression")

    # Error on any unknown tokens
    for n in stack:
        if isinstance(n, Token):
            raise ParseError(n, f"Unknown Expression '{n.value}'")

    # Error on any unconsumed reserved keywords
    for n in stack:
        if isinstance(n, ReservedToken):
            raise ParseError(n, f"Unexpected '{n.value}'")

    if len(stack) > 1:
        raise ParseError(stack[1], "Unexpected Expression")

    return stack.pop()._simplify()


def parse(s, *, allow_unknown=False):
    def check_node(n):
        if isinstance(n, NotOp):
            raise ParseError(n, "Expression not allowed in this context")

        for child in n.children:
            check_node(child)

    try:
        tokens = tokenize(s)
        n = create_ast(tokens, allow_unknown=allow_unknown)
        check_node(n)
        return n
    except ParseError as e:
        e.expression = s
        raise


def parse_match(s):
    try:
        tokens = tokenizer(s)
        return create_ast(tokens)
    except ParseError as e:
        e.expression = s
        raise
