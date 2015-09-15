# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# This is a copy on write dictionary and set which abuses classes to try and be nice and fast.
#
# Copyright (C) 2006 Tim Amsell
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
#Please Note:
# Be careful when using mutable types (ie Dict and Lists) - operations involving these are SLOW.
# Assign a file to __warn__ to get warnings about slow operations.
#

from __future__ import print_function
import copy
import types
ImmutableTypes = (
    types.NoneType,
    bool,
    complex,
    float,
    int,
    long,
    tuple,
    frozenset,
    basestring
)

MUTABLE = "__mutable__"

class COWMeta(type):
    pass

class COWDictMeta(COWMeta):
    __warn__ = False
    __hasmutable__ = False
    __marker__ = tuple()

    def __str__(cls):
        # FIXME: I have magic numbers!
        return "<COWDict Level: %i Current Keys: %i>" % (cls.__count__, len(cls.__dict__) - 3)
    __repr__ = __str__

    def cow(cls):
        class C(cls):
            __count__ = cls.__count__ + 1
        return C
    copy = cow
    __call__ = cow

    def __setitem__(cls, key, value):
        if not isinstance(value, ImmutableTypes):
            if not isinstance(value, COWMeta):
                cls.__hasmutable__ = True
            key += MUTABLE
        setattr(cls, key, value)

    def __getmutable__(cls, key, readonly=False):
        nkey = key + MUTABLE
        try:
            return cls.__dict__[nkey]
        except KeyError:
            pass

        value = getattr(cls, nkey)
        if readonly:
            return value

        if not cls.__warn__ is False and not isinstance(value, COWMeta):
            print("Warning: Doing a copy because %s is a mutable type." % key, file=cls.__warn__)
        try:
            value = value.copy()
        except AttributeError as e:
            value = copy.copy(value)
        setattr(cls, nkey, value)
        return value

    __getmarker__ = []
    def __getreadonly__(cls, key, default=__getmarker__):
        """\
        Get a value (even if mutable) which you promise not to change.
        """
        return cls.__getitem__(key, default, True)

    def __getitem__(cls, key, default=__getmarker__, readonly=False):
        try:
            try:
                value = getattr(cls, key)
            except AttributeError:
                value = cls.__getmutable__(key, readonly)

            # This is for values which have been deleted
            if value is cls.__marker__:
                raise AttributeError("key %s does not exist." % key)

            return value
        except AttributeError as e:
            if not default is cls.__getmarker__:
                return default

            raise KeyError(str(e))

    def __delitem__(cls, key):
        cls.__setitem__(key, cls.__marker__)

    def __revertitem__(cls, key):
        if not cls.__dict__.has_key(key):
            key += MUTABLE
        delattr(cls, key)

    def __contains__(cls, key):
        return cls.has_key(key)

    def has_key(cls, key):
        value = cls.__getreadonly__(key, cls.__marker__)
        if value is cls.__marker__:
            return False
        return True

    def iter(cls, type, readonly=False):
        for key in dir(cls):
            if key.startswith("__"):
                continue

            if key.endswith(MUTABLE):
                key = key[:-len(MUTABLE)]

            if type == "keys":
                yield key

            try:
                if readonly:
                    value = cls.__getreadonly__(key)
                else:
                    value = cls[key]
            except KeyError:
                continue

            if type == "values":
                yield value
            if type == "items":
                yield (key, value)
        raise StopIteration()

    def iterkeys(cls):
        return cls.iter("keys")
    def itervalues(cls, readonly=False):
        if not cls.__warn__ is False and cls.__hasmutable__ and readonly is False:
            print("Warning: If you arn't going to change any of the values call with True.", file=cls.__warn__)
        return cls.iter("values", readonly)
    def iteritems(cls, readonly=False):
        if not cls.__warn__ is False and cls.__hasmutable__ and readonly is False:
            print("Warning: If you arn't going to change any of the values call with True.", file=cls.__warn__)
        return cls.iter("items", readonly)

class COWSetMeta(COWDictMeta):
    def __str__(cls):
        # FIXME: I have magic numbers!
        return "<COWSet Level: %i Current Keys: %i>" % (cls.__count__, len(cls.__dict__) -3)
    __repr__ = __str__

    def cow(cls):
        class C(cls):
            __count__ = cls.__count__ + 1
        return C

    def add(cls, value):
        COWDictMeta.__setitem__(cls, repr(hash(value)), value)

    def remove(cls, value):
        COWDictMeta.__delitem__(cls, repr(hash(value)))

    def __in__(cls, value):
        return COWDictMeta.has_key(repr(hash(value)))

    def iterkeys(cls):
        raise TypeError("sets don't have keys")

    def iteritems(cls):
        raise TypeError("sets don't have 'items'")

# These are the actual classes you use!
class COWDictBase(object):
    __metaclass__ = COWDictMeta
    __count__ = 0

class COWSetBase(object):
    __metaclass__ = COWSetMeta
    __count__ = 0

if __name__ == "__main__":
    import sys
    COWDictBase.__warn__ = sys.stderr
    a = COWDictBase()
    print("a", a)

    a['a'] = 'a'
    a['b'] = 'b'
    a['dict'] = {}

    b = a.copy()
    print("b", b)
    b['c'] = 'b'

    print()

    print("a", a)
    for x in a.iteritems():
        print(x)
    print("--")
    print("b", b)
    for x in b.iteritems():
        print(x)
    print()

    b['dict']['a'] = 'b'
    b['a'] = 'c'

    print("a", a)
    for x in a.iteritems():
        print(x)
    print("--")
    print("b", b)
    for x in b.iteritems():
        print(x)
    print()

    try:
        b['dict2']
    except KeyError as e:
        print("Okay!")

    a['set'] = COWSetBase()
    a['set'].add("o1")
    a['set'].add("o1")
    a['set'].add("o2")

    print("a", a)
    for x in a['set'].itervalues():
        print(x)
    print("--")
    print("b", b)
    for x in b['set'].itervalues():
        print(x)
    print()

    b['set'].add('o3')

    print("a", a)
    for x in a['set'].itervalues():
        print(x)
    print("--")
    print("b", b)
    for x in b['set'].itervalues():
        print(x)
    print()

    a['set2'] = set()
    a['set2'].add("o1")
    a['set2'].add("o1")
    a['set2'].add("o2")

    print("a", a)
    for x in a.iteritems():
        print(x)
    print("--")
    print("b", b)
    for x in b.iteritems(readonly=True):
        print(x)
    print()

    del b['b']
    try:
        print(b['b'])
    except KeyError:
        print("Yay! deleted key raises error")

    if b.has_key('b'):
        print("Boo!")
    else:
        print("Yay - has_key with delete works!")

    print("a", a)
    for x in a.iteritems():
        print(x)
    print("--")
    print("b", b)
    for x in b.iteritems(readonly=True):
        print(x)
    print()

    b.__revertitem__('b')

    print("a", a)
    for x in a.iteritems():
        print(x)
    print("--")
    print("b", b)
    for x in b.iteritems(readonly=True):
        print(x)
    print()

    b.__revertitem__('dict')
    print("a", a)
    for x in a.iteritems():
        print(x)
    print("--")
    print("b", b)
    for x in b.iteritems(readonly=True):
        print(x)
    print()
