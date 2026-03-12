#
# Copyright (C) 2025 Garmin Ltd. or its subsidiaries
#
# SPDX-License-Identifier: GPL-2.0-only
#

import builtins

# Purposely blank out __builtins__ which prevents users from
# calling any normal builtin python functions
FILTERS = {
    "__builtins__": {},
}

CACHE = {}


def apply_filters(val, expressions):
    g = FILTERS.copy()

    for e in expressions:
        e = e.strip()
        if not e:
            continue

        k = (val, e)
        if k not in CACHE:
            # Set val as a local so it can be cleared out while keeping the
            # globals
            l = {"val": val}

            CACHE[k] = eval(e, g, l)

        val = CACHE[k]

    return val


class Namespace(object):
    """
    Helper class to simulate a python namespace. The object properties can be
    set as if it were a dictionary. Properties cannot be changed or deleted
    through the object interface
    """

    def __getitem__(self, name):
        return self.__dict__[name]

    def __setitem__(self, name, value):
        self.__dict__[name] = value

    def __contains__(self, name):
        return name in self.__dict__

    def __setattr__(self, name, value):
        raise AttributeError(f"Attribute {name!r} cannot be changed")

    def __delattr__(self, name):
        raise AttributeError(f"Attribute {name!r} cannot be deleted")


def filter_proc(*, name=None):
    """
    Decorator to mark a function that can be called in `apply_filters`, either
    directly in a filter expression, or indirectly. The `name` argument can be
    used to specify an alternate name for the function if the actual name is
    not desired. The `name` can be a fully qualified namespace if desired.

    All functions must be "pure" in that they do not depend on global state and
    have no global side effects (e.g. the output only depends on the input
    arguments); the results of filter expressions are cached to optimize
    repeated calls.
    """

    def inner(func):
        global FILTERS
        nonlocal name

        if name is None:
            name = func.__name__

        ns = name.split(".")
        o = FILTERS
        for n in ns[:-1]:
            if not n in o:
                o[n] = Namespace()
            o = o[n]

        o[ns[-1]] = func

        return func

    return inner


# A select set of builtins that are supported in filter expressions
filter_proc()(all)
filter_proc()(all)
filter_proc()(any)
filter_proc()(bin)
filter_proc()(bool)
filter_proc()(chr)
filter_proc()(enumerate)
filter_proc()(float)
filter_proc()(format)
filter_proc()(hex)
filter_proc()(int)
filter_proc()(len)
filter_proc()(map)
filter_proc()(max)
filter_proc()(min)
filter_proc()(oct)
filter_proc()(ord)
filter_proc()(pow)
filter_proc()(str)
filter_proc()(sum)


@filter_proc()
def suffix(val, suffix):
    return " ".join(v + suffix for v in val.split())


@filter_proc()
def prefix(val, prefix):
    return " ".join(prefix + v for v in val.split())


@filter_proc()
def sort(val):
    return " ".join(sorted(val.split()))


@filter_proc()
def remove(val, remove, sep=None):
    if isinstance(remove, str):
        remove = remove.split(sep)
    new = [i for i in val.split(sep) if not i in remove]

    if not sep:
        return " ".join(new)
    return sep.join(new)
