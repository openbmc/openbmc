# http://code.activestate.com/recipes/577629-namedtupleabc-abstract-base-class-mix-in-for-named/
# Copyright (c) 2011 Jan Kaliszewski (zuo). Available under the MIT License.
#
# SPDX-License-Identifier: MIT
#

"""
namedtuple_with_abc.py:
* named tuple mix-in + ABC (abstract base class) recipe,
* works under Python 2.6, 2.7 as well as 3.x.

Import this module to patch collections.namedtuple() factory function
-- enriching it with the 'abc' attribute (an abstract base class + mix-in
for named tuples) and decorating it with a wrapper that registers each
newly created named tuple as a subclass of namedtuple.abc.

How to import:
    import collections, namedtuple_with_abc
or:
    import namedtuple_with_abc
    from collections import namedtuple
    # ^ in this variant you must import namedtuple function
    #   *after* importing namedtuple_with_abc module
or simply:
    from namedtuple_with_abc import namedtuple

Simple usage example:
    class Credentials(namedtuple.abc):
        _fields = 'username password'
        def __str__(self):
            return ('{0.__class__.__name__}'
                    '(username={0.username}, password=...)'.format(self))
    print(Credentials("alice", "Alice's password"))

For more advanced examples -- see below the "if __name__ == '__main__':".
"""

import collections
from abc import ABCMeta, abstractproperty
from functools import wraps
from sys import version_info

__all__ = ('namedtuple',)
_namedtuple = collections.namedtuple


class _NamedTupleABCMeta(ABCMeta):
    '''The metaclass for the abstract base class + mix-in for named tuples.'''
    def __new__(mcls, name, bases, namespace):
        fields = namespace.get('_fields')
        for base in bases:
            if fields is not None:
                break
            fields = getattr(base, '_fields', None)
        if not isinstance(fields, abstractproperty):
            basetuple = _namedtuple(name, fields)
            bases = (basetuple,) + bases
            namespace.pop('_fields', None)
            namespace.setdefault('__doc__', basetuple.__doc__)
            namespace.setdefault('__slots__', ())
        return ABCMeta.__new__(mcls, name, bases, namespace)


exec(
    # Python 2.x metaclass declaration syntax
    """class _NamedTupleABC(object):
        '''The abstract base class + mix-in for named tuples.'''
        __metaclass__ = _NamedTupleABCMeta
        _fields = abstractproperty()""" if version_info[0] < 3 else
    # Python 3.x metaclass declaration syntax
    """class _NamedTupleABC(metaclass=_NamedTupleABCMeta):
        '''The abstract base class + mix-in for named tuples.'''
        _fields = abstractproperty()"""
)


_namedtuple.abc = _NamedTupleABC
#_NamedTupleABC.register(type(version_info))  # (and similar, in the future...)

@wraps(_namedtuple)
def namedtuple(*args, **kwargs):
    '''Named tuple factory with namedtuple.abc subclass registration.'''
    cls = _namedtuple(*args, **kwargs)
    _NamedTupleABC.register(cls)
    return cls

collections.namedtuple = namedtuple




if __name__ == '__main__':

    '''Examples and explanations'''

    # Simple usage

    class MyRecord(namedtuple.abc):
        _fields = 'x y z'  # such form will be transformed into ('x', 'y', 'z')
        def _my_custom_method(self):
            return list(self._asdict().items())
    # (the '_fields' attribute belongs to the named tuple public API anyway)

    rec = MyRecord(1, 2, 3)
    print(rec)
    print(rec._my_custom_method())
    print(rec._replace(y=222))
    print(rec._replace(y=222)._my_custom_method())

    # Custom abstract classes...

    class MyAbstractRecord(namedtuple.abc):
        def _my_custom_method(self):
            return list(self._asdict().items())

    try:
        MyAbstractRecord()  # (abstract classes cannot be instantiated)
    except TypeError as exc:
        print(exc)

    class AnotherAbstractRecord(MyAbstractRecord):
        def __str__(self):
            return '<<<{0}>>>'.format(super(AnotherAbstractRecord,
                                            self).__str__())

    # ...and their non-abstract subclasses

    class MyRecord2(MyAbstractRecord):
        _fields = 'a, b'

    class MyRecord3(AnotherAbstractRecord):
        _fields = 'p', 'q', 'r'

    rec2 = MyRecord2('foo', 'bar')
    print(rec2)
    print(rec2._my_custom_method())
    print(rec2._replace(b=222))
    print(rec2._replace(b=222)._my_custom_method())

    rec3 = MyRecord3('foo', 'bar', 'baz')
    print(rec3)
    print(rec3._my_custom_method())
    print(rec3._replace(q=222))
    print(rec3._replace(q=222)._my_custom_method())

   # You can also subclass non-abstract ones...

    class MyRecord33(MyRecord3):
        def __str__(self):
            return '< {0!r}, ..., {0!r} >'.format(self.p, self.r)

    rec33 = MyRecord33('foo', 'bar', 'baz')
    print(rec33)
    print(rec33._my_custom_method())
    print(rec33._replace(q=222))
    print(rec33._replace(q=222)._my_custom_method())

    # ...and even override the magic '_fields' attribute again

    class MyRecord345(MyRecord3):
        _fields = 'e f g h i j k'

    rec345 = MyRecord345(1, 2, 3, 4, 3, 2, 1)
    print(rec345)
    print(rec345._my_custom_method())
    print(rec345._replace(f=222))
    print(rec345._replace(f=222)._my_custom_method())

    # Mixing-in some other classes is also possible:

    class MyMixIn(object):
        def method(self):
            return "MyMixIn.method() called"
        def _my_custom_method(self):
            return "MyMixIn._my_custom_method() called"
        def count(self, item):
            return "MyMixIn.count({0}) called".format(item)
        def _asdict(self):  # (cannot override a namedtuple method, see below)
            return "MyMixIn._asdict() called"

    class MyRecord4(MyRecord33, MyMixIn):  # mix-in on the right
        _fields = 'j k l x'

    class MyRecord5(MyMixIn, MyRecord33):  # mix-in on the left
        _fields = 'j k l x y'

    rec4 = MyRecord4(1, 2, 3, 2)
    print(rec4)
    print(rec4.method())
    print(rec4._my_custom_method())  # MyRecord33's
    print(rec4.count(2))  # tuple's
    print(rec4._replace(k=222))
    print(rec4._replace(k=222).method())
    print(rec4._replace(k=222)._my_custom_method())  # MyRecord33's
    print(rec4._replace(k=222).count(8))  # tuple's

    rec5 = MyRecord5(1, 2, 3, 2, 1)
    print(rec5)
    print(rec5.method())
    print(rec5._my_custom_method())  # MyMixIn's
    print(rec5.count(2))  # MyMixIn's
    print(rec5._replace(k=222))
    print(rec5._replace(k=222).method())
    print(rec5._replace(k=222)._my_custom_method())  # MyMixIn's
    print(rec5._replace(k=222).count(2))  # MyMixIn's

    # Note that behavior: the standard namedtuple methods cannot be
    # overridden by a foreign mix-in -- even if the mix-in is declared
    # as the leftmost base class (but, obviously, you can override them
    # in the defined class or its subclasses):

    print(rec4._asdict())  # (returns a dict, not "MyMixIn._asdict() called")
    print(rec5._asdict())  # (returns a dict, not "MyMixIn._asdict() called")

    class MyRecord6(MyRecord33):
        _fields = 'j k l x y z'
        def _asdict(self):
            return "MyRecord6._asdict() called"
    rec6 = MyRecord6(1, 2, 3, 1, 2, 3)
    print(rec6._asdict())  # (this returns "MyRecord6._asdict() called")

    # All that record classes are real subclasses of namedtuple.abc:

    assert issubclass(MyRecord, namedtuple.abc)
    assert issubclass(MyAbstractRecord, namedtuple.abc)
    assert issubclass(AnotherAbstractRecord, namedtuple.abc)
    assert issubclass(MyRecord2, namedtuple.abc)
    assert issubclass(MyRecord3, namedtuple.abc)
    assert issubclass(MyRecord33, namedtuple.abc)
    assert issubclass(MyRecord345, namedtuple.abc)
    assert issubclass(MyRecord4, namedtuple.abc)
    assert issubclass(MyRecord5, namedtuple.abc)
    assert issubclass(MyRecord6, namedtuple.abc)

    # ...but abstract ones are not subclasses of tuple
    # (and this is what you probably want):

    assert not issubclass(MyAbstractRecord, tuple)
    assert not issubclass(AnotherAbstractRecord, tuple)

    assert issubclass(MyRecord, tuple)
    assert issubclass(MyRecord2, tuple)
    assert issubclass(MyRecord3, tuple)
    assert issubclass(MyRecord33, tuple)
    assert issubclass(MyRecord345, tuple)
    assert issubclass(MyRecord4, tuple)
    assert issubclass(MyRecord5, tuple)
    assert issubclass(MyRecord6, tuple)

    # Named tuple classes created with namedtuple() factory function
    # (in the "traditional" way) are registered as "virtual" subclasses
    # of namedtuple.abc:

    MyTuple = namedtuple('MyTuple', 'a b c')
    mt = MyTuple(1, 2, 3)
    assert issubclass(MyTuple, namedtuple.abc)
    assert isinstance(mt, namedtuple.abc)
