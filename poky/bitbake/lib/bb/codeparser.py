#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

"""
BitBake code parser

Parses actual code (i.e. python and shell) for functions and in-line
expressions. Used mainly to determine dependencies on other functions
and variables within the BitBake metadata. Also provides a cache for
this information in order to speed up processing.

(Not to be confused with the code that parses the metadata itself,
see lib/bb/parse/ for that).

NOTE: if you change how the parsers gather information you will almost
certainly need to increment CodeParserCache.CACHE_VERSION below so that
any existing codeparser cache gets invalidated. Additionally you'll need
to increment __cache_version__ in cache.py in order to ensure that old
recipe caches don't trigger "Taskhash mismatch" errors.

"""

import ast
import sys
import codegen
import logging
import inspect
import bb.pysh as pysh
import bb.utils, bb.data
import hashlib
from itertools import chain
from bb.pysh import pyshyacc, pyshlex
from bb.cache import MultiProcessCache

logger = logging.getLogger('BitBake.CodeParser')

def bbhash(s):
    return hashlib.sha256(s.encode("utf-8")).hexdigest()

def check_indent(codestr):
    """If the code is indented, add a top level piece of code to 'remove' the indentation"""

    i = 0
    while codestr[i] in ["\n", "\t", " "]:
        i = i + 1

    if i == 0:
        return codestr

    if codestr[i-1] == "\t" or codestr[i-1] == " ":
        if codestr[0] == "\n":
            # Since we're adding a line, we need to remove one line of any empty padding
            # to ensure line numbers are correct
            codestr = codestr[1:]
        return "if 1:\n" + codestr

    return codestr

modulecode_deps = {}

def add_module_functions(fn, functions, namespace):
    import os
    fstat = os.stat(fn)
    fixedhash = fn + ":" + str(fstat.st_size) +  ":" + str(fstat.st_mtime)
    for f in functions:
        name = "%s.%s" % (namespace, f)
        parser = PythonParser(name, logger)
        try:
            parser.parse_python(None, filename=fn, lineno=1, fixedhash=fixedhash+f)
            #bb.warn("Cached %s" % f)
        except KeyError:
            targetfn = inspect.getsourcefile(functions[f])
            if fn != targetfn:
                # Skip references to other modules outside this file
                #bb.warn("Skipping %s" % name)
                continue
            lines, lineno = inspect.getsourcelines(functions[f])
            src = "".join(lines)
            parser.parse_python(src, filename=fn, lineno=lineno, fixedhash=fixedhash+f)
            #bb.warn("Not cached %s" % f)
        execs = parser.execs.copy()
        # Expand internal module exec references
        for e in parser.execs:
            if e in functions:
                execs.remove(e)
                execs.add(namespace + "." + e)
        visitorcode = None
        if hasattr(functions[f], 'visitorcode'):
            visitorcode = getattr(functions[f], "visitorcode")
        modulecode_deps[name] = [parser.references.copy(), execs, parser.var_execs.copy(), parser.contains.copy(), parser.extra, visitorcode]
        #bb.warn("%s: %s\nRefs:%s Execs: %s %s %s" % (name, fn, parser.references, parser.execs, parser.var_execs, parser.contains))

def update_module_dependencies(d):
    for mod in modulecode_deps:
        excludes = set((d.getVarFlag(mod, "vardepsexclude") or "").split())
        if excludes:
            modulecode_deps[mod] = [modulecode_deps[mod][0] - excludes, modulecode_deps[mod][1] - excludes, modulecode_deps[mod][2] - excludes, modulecode_deps[mod][3], modulecode_deps[mod][4], modulecode_deps[mod][5]]

# A custom getstate/setstate using tuples is actually worth 15% cachesize by
# avoiding duplication of the attribute names!
class SetCache(object):
    def __init__(self):
        self.setcache = {}

    def internSet(self, items):
        
        new = []
        for i in items:
            new.append(sys.intern(i))
        s = frozenset(new)
        h = hash(s)
        if h in self.setcache:
            return self.setcache[h]
        self.setcache[h] = s
        return s

codecache = SetCache()

class pythonCacheLine(object):
    def __init__(self, refs, execs, contains, extra):
        self.refs = codecache.internSet(refs)
        self.execs = codecache.internSet(execs)
        self.contains = {}
        for c in contains:
            self.contains[c] = codecache.internSet(contains[c])
        self.extra = extra

    def __getstate__(self):
        return (self.refs, self.execs, self.contains, self.extra)

    def __setstate__(self, state):
        (refs, execs, contains, extra) = state
        self.__init__(refs, execs, contains, extra)
    def __hash__(self):
        l = (hash(self.refs), hash(self.execs), hash(self.extra))
        for c in sorted(self.contains.keys()):
            l = l + (c, hash(self.contains[c]))
        return hash(l)
    def __repr__(self):
        return " ".join([str(self.refs), str(self.execs), str(self.contains)]) 


class shellCacheLine(object):
    def __init__(self, execs):
        self.execs = codecache.internSet(execs)

    def __getstate__(self):
        return (self.execs)

    def __setstate__(self, state):
        (execs) = state
        self.__init__(execs)
    def __hash__(self):
        return hash(self.execs)
    def __repr__(self):
        return str(self.execs)

class CodeParserCache(MultiProcessCache):
    cache_file_name = "bb_codeparser.dat"
    # NOTE: you must increment this if you change how the parsers gather information,
    # so that an existing cache gets invalidated. Additionally you'll need
    # to increment __cache_version__ in cache.py in order to ensure that old
    # recipe caches don't trigger "Taskhash mismatch" errors.
    CACHE_VERSION = 14

    def __init__(self):
        MultiProcessCache.__init__(self)
        self.pythoncache = self.cachedata[0]
        self.shellcache = self.cachedata[1]
        self.pythoncacheextras = self.cachedata_extras[0]
        self.shellcacheextras = self.cachedata_extras[1]

        # To avoid duplication in the codeparser cache, keep
        # a lookup of hashes of objects we already have
        self.pythoncachelines = {}
        self.shellcachelines = {}

    def newPythonCacheLine(self, refs, execs, contains, extra):
        cacheline = pythonCacheLine(refs, execs, contains, extra)
        h = hash(cacheline)
        if h in self.pythoncachelines:
            return self.pythoncachelines[h]
        self.pythoncachelines[h] = cacheline
        return cacheline

    def newShellCacheLine(self, execs):
        cacheline = shellCacheLine(execs)
        h = hash(cacheline)
        if h in self.shellcachelines:
            return self.shellcachelines[h]
        self.shellcachelines[h] = cacheline
        return cacheline

    def init_cache(self, cachedir):
        # Check if we already have the caches
        if self.pythoncache:
            return

        MultiProcessCache.init_cache(self, cachedir)

        # cachedata gets re-assigned in the parent
        self.pythoncache = self.cachedata[0]
        self.shellcache = self.cachedata[1]

    def create_cachedata(self):
        data = [{}, {}]
        return data

codeparsercache = CodeParserCache()

def parser_cache_init(cachedir):
    codeparsercache.init_cache(cachedir)

def parser_cache_save():
    codeparsercache.save_extras()

def parser_cache_savemerge():
    codeparsercache.save_merge()

Logger = logging.getLoggerClass()
class BufferedLogger(Logger):
    def __init__(self, name, level=0, target=None):
        Logger.__init__(self, name)
        self.setLevel(level)
        self.buffer = []
        self.target = target

    def handle(self, record):
        self.buffer.append(record)

    def flush(self):
        for record in self.buffer:
            if self.target.isEnabledFor(record.levelno):
                self.target.handle(record)
        self.buffer = []

class DummyLogger():
    def flush(self):
        return

class PythonParser():
    getvars = (".getVar", ".appendVar", ".prependVar", "oe.utils.conditional")
    getvarflags = (".getVarFlag", ".appendVarFlag", ".prependVarFlag")
    containsfuncs = ("bb.utils.contains", "base_contains")
    containsanyfuncs = ("bb.utils.contains_any",  "bb.utils.filter")
    execfuncs = ("bb.build.exec_func", "bb.build.exec_task")

    def warn(self, func, arg):
        """Warn about calls of bitbake APIs which pass a non-literal
        argument for the variable name, as we're not able to track such
        a reference.
        """

        try:
            funcstr = codegen.to_source(func)
            argstr = codegen.to_source(arg)
        except TypeError:
            self.log.debug2('Failed to convert function and argument to source form')
        else:
            self.log.debug(self.unhandled_message % (funcstr, argstr))

    def visit_Call(self, node):
        name = self.called_node_name(node.func)
        if name and name in modulecode_deps and modulecode_deps[name][5]:
            visitorcode = modulecode_deps[name][5]
            contains, execs, warn = visitorcode(name, node.args)
            for i in contains:
                self.contains[i] = contains[i]
            self.execs |= execs
            if warn:
                self.warn(node.func, warn)
        elif name and (name.endswith(self.getvars) or name.endswith(self.getvarflags) or name in self.containsfuncs or name in self.containsanyfuncs):
            if isinstance(node.args[0], ast.Constant) and isinstance(node.args[0].value, str):
                varname = node.args[0].value
                if name in self.containsfuncs and isinstance(node.args[1], ast.Constant):
                    if varname not in self.contains:
                        self.contains[varname] = set()
                    self.contains[varname].add(node.args[1].value)
                elif name in self.containsanyfuncs and isinstance(node.args[1], ast.Constant):
                    if varname not in self.contains:
                        self.contains[varname] = set()
                    self.contains[varname].update(node.args[1].value.split())
                elif name.endswith(self.getvarflags):
                    if isinstance(node.args[1], ast.Constant):
                        self.references.add('%s[%s]' % (varname, node.args[1].value))
                    else:
                        self.warn(node.func, node.args[1])
                else:
                    self.references.add(varname)
            else:
                self.warn(node.func, node.args[0])
        elif name and name.endswith(".expand"):
            if isinstance(node.args[0], ast.Constant):
                value = node.args[0].value
                d = bb.data.init()
                parser = d.expandWithRefs(value, self.name)
                self.references |= parser.references
                self.execs |= parser.execs
                for varname in parser.contains:
                    if varname not in self.contains:
                        self.contains[varname] = set()
                    self.contains[varname] |= parser.contains[varname]
        elif name in self.execfuncs:
            if isinstance(node.args[0], ast.Constant):
                self.var_execs.add(node.args[0].value)
            else:
                self.warn(node.func, node.args[0])
        elif name and isinstance(node.func, (ast.Name, ast.Attribute)):
            self.execs.add(name)

    def called_node_name(self, node):
        """Given a called node, return its original string form"""
        components = []
        while node:
            if isinstance(node, ast.Attribute):
                components.append(node.attr)
                node = node.value
            elif isinstance(node, ast.Name):
                components.append(node.id)
                return '.'.join(reversed(components))
            else:
                break

    def __init__(self, name, log):
        self.name = name
        self.var_execs = set()
        self.contains = {}
        self.execs = set()
        self.references = set()
        self._log = log
        # Defer init as expensive
        self.log = DummyLogger()

        self.unhandled_message = "in call of %s, argument '%s' is not a string literal"
        self.unhandled_message = "while parsing %s, %s" % (name, self.unhandled_message)

    # For the python module code it is expensive to have the function text so it is
    # uses a different fixedhash to cache against. We can take the hit on obtaining the
    # text if it isn't in the cache.
    def parse_python(self, node, lineno=0, filename="<string>", fixedhash=None):
        if not fixedhash and (not node or not node.strip()):
            return

        if fixedhash:
            h = fixedhash
        else:
            h = bbhash(str(node))

        if h in codeparsercache.pythoncache:
            self.references = set(codeparsercache.pythoncache[h].refs)
            self.execs = set(codeparsercache.pythoncache[h].execs)
            self.contains = {}
            for i in codeparsercache.pythoncache[h].contains:
                self.contains[i] = set(codeparsercache.pythoncache[h].contains[i])
            self.extra = codeparsercache.pythoncache[h].extra
            return

        if h in codeparsercache.pythoncacheextras:
            self.references = set(codeparsercache.pythoncacheextras[h].refs)
            self.execs = set(codeparsercache.pythoncacheextras[h].execs)
            self.contains = {}
            for i in codeparsercache.pythoncacheextras[h].contains:
                self.contains[i] = set(codeparsercache.pythoncacheextras[h].contains[i])
            self.extra = codeparsercache.pythoncacheextras[h].extra
            return

        if fixedhash and not node:
            raise KeyError

        # Need to parse so take the hit on the real log buffer
        self.log = BufferedLogger('BitBake.Data.PythonParser', logging.DEBUG, self._log)

        # We can't add to the linenumbers for compile, we can pad to the correct number of blank lines though
        node = "\n" * int(lineno) + node
        code = compile(check_indent(str(node)), filename, "exec",
                       ast.PyCF_ONLY_AST)

        for n in ast.walk(code):
            if n.__class__.__name__ == "Call":
                self.visit_Call(n)

        self.execs.update(self.var_execs)
        self.extra = None
        if fixedhash:
            self.extra = bbhash(str(node))

        codeparsercache.pythoncacheextras[h] = codeparsercache.newPythonCacheLine(self.references, self.execs, self.contains, self.extra)

class ShellParser():
    def __init__(self, name, log):
        self.funcdefs = set()
        self.allexecs = set()
        self.execs = set()
        self._name = name
        self._log = log
        # Defer init as expensive
        self.log = DummyLogger()

        self.unhandled_template = "unable to handle non-literal command '%s'"
        self.unhandled_template = "while parsing %s, %s" % (name, self.unhandled_template)

    def parse_shell(self, value):
        """Parse the supplied shell code in a string, returning the external
        commands it executes.
        """

        h = bbhash(str(value))

        if h in codeparsercache.shellcache:
            self.execs = set(codeparsercache.shellcache[h].execs)
            return self.execs

        if h in codeparsercache.shellcacheextras:
            self.execs = set(codeparsercache.shellcacheextras[h].execs)
            return self.execs

        # Need to parse so take the hit on the real log buffer
        self.log = BufferedLogger('BitBake.Data.%s' % self._name, logging.DEBUG, self._log)

        self._parse_shell(value)
        self.execs = set(cmd for cmd in self.allexecs if cmd not in self.funcdefs)

        codeparsercache.shellcacheextras[h] = codeparsercache.newShellCacheLine(self.execs)

        return self.execs

    def _parse_shell(self, value):
        try:
            tokens, _ = pyshyacc.parse(value, eof=True, debug=False)
        except Exception:
            bb.error('Error during parse shell code, the last 5 lines are:\n%s' % '\n'.join(value.split('\n')[-5:]))
            raise

        self.process_tokens(tokens)

    def process_tokens(self, tokens):
        """Process a supplied portion of the syntax tree as returned by
        pyshyacc.parse.
        """

        def function_definition(value):
            self.funcdefs.add(value.name)
            return [value.body], None

        def case_clause(value):
            # Element 0 of each item in the case is the list of patterns, and
            # Element 1 of each item in the case is the list of commands to be
            # executed when that pattern matches.
            words = chain(*[item[0] for item in value.items])
            cmds  = chain(*[item[1] for item in value.items])
            return cmds, words

        def if_clause(value):
            main = chain(value.cond, value.if_cmds)
            rest = value.else_cmds
            if isinstance(rest, tuple) and rest[0] == "elif":
                return chain(main, if_clause(rest[1]))
            else:
                return chain(main, rest)

        def simple_command(value):
            return None, chain(value.words, (assign[1] for assign in value.assigns))

        token_handlers = {
            "and_or": lambda x: ((x.left, x.right), None),
            "async": lambda x: ([x], None),
            "brace_group": lambda x: (x.cmds, None),
            "for_clause": lambda x: (x.cmds, x.items),
            "function_definition": function_definition,
            "if_clause": lambda x: (if_clause(x), None),
            "pipeline": lambda x: (x.commands, None),
            "redirect_list": lambda x: ([x.cmd], None),
            "subshell": lambda x: (x.cmds, None),
            "while_clause": lambda x: (chain(x.condition, x.cmds), None),
            "until_clause": lambda x: (chain(x.condition, x.cmds), None),
            "simple_command": simple_command,
            "case_clause": case_clause,
        }

        def process_token_list(tokens):
            for token in tokens:
                if isinstance(token, list):
                    process_token_list(token)
                    continue
                name, value = token
                try:
                    more_tokens, words = token_handlers[name](value)
                except KeyError:
                    raise NotImplementedError("Unsupported token type " + name)

                if more_tokens:
                    self.process_tokens(more_tokens)

                if words:
                    self.process_words(words)

        process_token_list(tokens)

    def process_words(self, words):
        """Process a set of 'words' in pyshyacc parlance, which includes
        extraction of executed commands from $() blocks, as well as grabbing
        the command name argument.
        """

        words = list(words)
        for word in words:
            wtree = pyshlex.make_wordtree(word[1])
            for part in wtree:
                if not isinstance(part, list):
                    continue

                candidates = [part]

                # If command is of type:
                #
                #   var="... $(cmd [...]) ..."
                #
                # Then iterate on what's between the quotes and if we find a
                # list, make that what we check for below.
                if len(part) >= 3 and part[0] == '"':
                    for p in part[1:-1]:
                        if isinstance(p, list):
                            candidates.append(p)

                for candidate in candidates:
                    if len(candidate) >= 2:
                        if candidate[0] in ('`', '$('):
                            command = pyshlex.wordtree_as_string(candidate[1:-1])
                            self._parse_shell(command)

                            if word[0] in ("cmd_name", "cmd_word"):
                                if word in words:
                                    words.remove(word)

        usetoken = False
        for word in words:
            if word[0] in ("cmd_name", "cmd_word") or \
               (usetoken and word[0] == "TOKEN"):
                if "=" in word[1]:
                    usetoken = True
                    continue

                cmd = word[1]
                if cmd.startswith("$"):
                    self.log.debug(self.unhandled_template % cmd)
                elif cmd == "eval":
                    command = " ".join(word for _, word in words[1:])
                    self._parse_shell(command)
                else:
                    self.allexecs.add(cmd)
                break
