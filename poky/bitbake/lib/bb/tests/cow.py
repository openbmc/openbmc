#
# BitBake Tests for Copy-on-Write (cow.py)
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Copyright 2006 Holger Freyther <freyther@handhelds.org>
# Copyright (C) 2020  Agilent Technologies, Inc.
#

import io
import re
import sys
import unittest
import contextlib
import collections

from bb.COW import COWDictBase, COWSetBase, COWDictMeta, COWSetMeta


class COWTestCase(unittest.TestCase):
    """
    Test case for the COW module from mithro
    """

    def setUp(self):
        self._track_warnings = False
        self._warning_file = io.StringIO()
        self._unhandled_warnings = collections.deque()
        COWDictBase.__warn__ = self._warning_file

    def tearDown(self):
        COWDictBase.__warn__ = sys.stderr
        if self._track_warnings:
            self._checkAllWarningsRead()

    def trackWarnings(self):
        self._track_warnings = True

    def _collectWarnings(self):
        self._warning_file.seek(0)
        for warning in self._warning_file:
            self._unhandled_warnings.append(warning.rstrip("\n"))
        self._warning_file.truncate(0)
        self._warning_file.seek(0)

    def _checkAllWarningsRead(self):
        self._collectWarnings()
        self.assertSequenceEqual(self._unhandled_warnings, [])

    @contextlib.contextmanager
    def checkReportsWarning(self, expected_warning):
        self._checkAllWarningsRead()
        yield
        self._collectWarnings()
        warning = self._unhandled_warnings.popleft()
        self.assertEqual(warning, expected_warning)

    def checkStrOutput(self, obj, expected_levels, expected_keys):
        if obj.__class__ is COWDictMeta:
            expected_class_name = "COWDict"
        elif obj.__class__ is COWSetMeta:
            expected_class_name = "COWSet"
        else:
            self.fail("obj is of unknown type {0}".format(type(obj)))
        s = str(obj)
        regex = re.compile(r"<(\w+) Level: (\d+) Current Keys: (\d+)>")
        match = regex.match(s)
        self.assertIsNotNone(match, "bad str output: '{0}'".format(s))
        class_name = match.group(1)
        self.assertEqual(class_name, expected_class_name)
        levels = int(match.group(2))
        self.assertEqual(levels, expected_levels, "wrong # levels in str: '{0}'".format(s))
        keys = int(match.group(3))
        self.assertEqual(keys, expected_keys, "wrong # keys in str: '{0}'".format(s))

    def testGetSet(self):
        """
        Test and set
        """
        a = COWDictBase.copy()

        self.assertEqual(False, 'a' in a)

        a['a'] = 'a'
        a['b'] = 'b'
        self.assertEqual(True, 'a' in a)
        self.assertEqual(True, 'b' in a)
        self.assertEqual('a', a['a'])
        self.assertEqual('b', a['b'])

    def testCopyCopy(self):
        """
        Test the copy of copies
        """

        # create two COW dict 'instances'
        b = COWDictBase.copy()
        c = COWDictBase.copy()

        # assign some keys to one instance, some keys to another
        b['a'] = 10
        b['c'] = 20
        c['a'] = 30

        # test separation of the two instances
        self.assertEqual(False, 'c' in c)
        self.assertEqual(30, c['a'])
        self.assertEqual(10, b['a'])

        # test copy
        b_2 = b.copy()
        c_2 = c.copy()

        self.assertEqual(False, 'c' in c_2)
        self.assertEqual(10, b_2['a'])

        b_2['d'] = 40
        self.assertEqual(False, 'd' in c_2)
        self.assertEqual(True, 'd' in b_2)
        self.assertEqual(40, b_2['d'])
        self.assertEqual(False, 'd' in b)
        self.assertEqual(False, 'd' in c)

        c_2['d'] = 30
        self.assertEqual(True, 'd' in c_2)
        self.assertEqual(True, 'd' in b_2)
        self.assertEqual(30, c_2['d'])
        self.assertEqual(40, b_2['d'])
        self.assertEqual(False, 'd' in b)
        self.assertEqual(False, 'd' in c)

        # test copy of the copy
        c_3 = c_2.copy()
        b_3 = b_2.copy()
        b_3_2 = b_2.copy()

        c_3['e'] = 4711
        self.assertEqual(4711, c_3['e'])
        self.assertEqual(False, 'e' in c_2)
        self.assertEqual(False, 'e' in b_3)
        self.assertEqual(False, 'e' in b_3_2)
        self.assertEqual(False, 'e' in b_2)

        b_3['e'] = 'viel'
        self.assertEqual('viel', b_3['e'])
        self.assertEqual(4711, c_3['e'])
        self.assertEqual(False, 'e' in c_2)
        self.assertEqual(True, 'e' in b_3)
        self.assertEqual(False, 'e' in b_3_2)
        self.assertEqual(False, 'e' in b_2)

    def testCow(self):
        self.trackWarnings()

        c = COWDictBase.copy()
        c['123'] = 1027
        c['other'] = 4711
        c['d'] = {'abc': 10, 'bcd': 20}

        copy = c.copy()

        self.assertEqual(1027, c['123'])
        self.assertEqual(4711, c['other'])
        self.assertEqual({'abc': 10, 'bcd': 20}, c['d'])
        self.assertEqual(1027, copy['123'])
        self.assertEqual(4711, copy['other'])
        with self.checkReportsWarning("Warning: Doing a copy because d is a mutable type."):
            self.assertEqual({'abc': 10, 'bcd': 20}, copy['d'])

        # cow it now
        copy['123'] = 1028
        copy['other'] = 4712
        copy['d']['abc'] = 20

        self.assertEqual(1027, c['123'])
        self.assertEqual(4711, c['other'])
        self.assertEqual({'abc': 10, 'bcd': 20}, c['d'])
        self.assertEqual(1028, copy['123'])
        self.assertEqual(4712, copy['other'])
        self.assertEqual({'abc': 20, 'bcd': 20}, copy['d'])

    def testOriginalTestSuite(self):
        # This test suite is a port of the original one from COW.py
        self.trackWarnings()

        a = COWDictBase.copy()
        self.checkStrOutput(a, 1, 0)

        a['a'] = 'a'
        a['b'] = 'b'
        a['dict'] = {}
        self.checkStrOutput(a, 1, 4)  # 4th member is dict__mutable__

        b = a.copy()
        self.checkStrOutput(b, 2, 0)
        b['c'] = 'b'
        self.checkStrOutput(b, 2, 1)

        with self.checkReportsWarning("Warning: If you aren't going to change any of the values call with True."):
            self.assertListEqual(list(a.iteritems()),
                                 [('a', 'a'),
                                  ('b', 'b'),
                                  ('dict', {})
                                  ])

        with self.checkReportsWarning("Warning: If you aren't going to change any of the values call with True."):
            b_gen = b.iteritems()
        self.assertTupleEqual(next(b_gen), ('a', 'a'))
        self.assertTupleEqual(next(b_gen), ('b', 'b'))
        self.assertTupleEqual(next(b_gen), ('c', 'b'))
        with self.checkReportsWarning("Warning: Doing a copy because dict is a mutable type."):
            self.assertTupleEqual(next(b_gen), ('dict', {}))
        with self.assertRaises(StopIteration):
            next(b_gen)

        b['dict']['a'] = 'b'
        b['a'] = 'c'

        self.checkStrOutput(a, 1, 4)
        self.checkStrOutput(b, 2, 3)

        with self.checkReportsWarning("Warning: If you aren't going to change any of the values call with True."):
            self.assertListEqual(list(a.iteritems()),
                                 [('a', 'a'),
                                  ('b', 'b'),
                                  ('dict', {})
                                  ])

        with self.checkReportsWarning("Warning: If you aren't going to change any of the values call with True."):
            b_gen = b.iteritems()
        self.assertTupleEqual(next(b_gen), ('a', 'c'))
        self.assertTupleEqual(next(b_gen), ('b', 'b'))
        self.assertTupleEqual(next(b_gen), ('c', 'b'))
        self.assertTupleEqual(next(b_gen), ('dict', {'a': 'b'}))
        with self.assertRaises(StopIteration):
            next(b_gen)

        with self.assertRaises(KeyError):
            print(b["dict2"])

        a['set'] = COWSetBase()
        a['set'].add("o1")
        a['set'].add("o1")
        a['set'].add("o2")
        self.assertSetEqual(set(a['set'].itervalues()), {"o1", "o2"})
        self.assertSetEqual(set(b['set'].itervalues()), {"o1", "o2"})

        b['set'].add('o3')
        self.assertSetEqual(set(a['set'].itervalues()), {"o1", "o2"})
        self.assertSetEqual(set(b['set'].itervalues()), {"o1", "o2", "o3"})

        a['set2'] = set()
        a['set2'].add("o1")
        a['set2'].add("o1")
        a['set2'].add("o2")

        # We don't expect 'a' to change anymore
        def check_a():
            with self.checkReportsWarning("Warning: If you aren't going to change any of the values call with True."):
                a_gen = a.iteritems()
                self.assertTupleEqual(next(a_gen), ('a', 'a'))
            self.assertTupleEqual(next(a_gen), ('b', 'b'))
            self.assertTupleEqual(next(a_gen), ('dict', {}))
            self.assertTupleEqual(next(a_gen), ('set2', {'o1', 'o2'}))
            a_sub_set = next(a_gen)
            self.assertEqual(a_sub_set[0], 'set')
            self.checkStrOutput(a_sub_set[1], 1, 2)
            self.assertSetEqual(set(a_sub_set[1].itervalues()), {'o1', 'o2'})

        check_a()

        b_gen = b.iteritems(readonly=True)
        self.assertTupleEqual(next(b_gen), ('a', 'c'))
        self.assertTupleEqual(next(b_gen), ('b', 'b'))
        self.assertTupleEqual(next(b_gen), ('c', 'b'))
        self.assertTupleEqual(next(b_gen), ('dict', {'a': 'b'}))
        self.assertTupleEqual(next(b_gen), ('set2', {'o1', 'o2'}))
        b_sub_set = next(b_gen)
        self.assertEqual(b_sub_set[0], 'set')
        self.checkStrOutput(b_sub_set[1], 2, 1)
        self.assertSetEqual(set(b_sub_set[1].itervalues()), {'o1', 'o2', 'o3'})

        del b['b']
        with self.assertRaises(KeyError):
            print(b['b'])
        self.assertFalse('b' in b)

        check_a()

        b.__revertitem__('b')
        check_a()
        self.assertEqual(b['b'], 'b')
        self.assertTrue('b' in b)

        b.__revertitem__('dict')
        check_a()

        b_gen = b.iteritems(readonly=True)
        self.assertTupleEqual(next(b_gen), ('a', 'c'))
        self.assertTupleEqual(next(b_gen), ('b', 'b'))
        self.assertTupleEqual(next(b_gen), ('c', 'b'))
        self.assertTupleEqual(next(b_gen), ('dict', {}))
        self.assertTupleEqual(next(b_gen), ('set2', {'o1', 'o2'}))
        b_sub_set = next(b_gen)
        self.assertEqual(b_sub_set[0], 'set')
        self.checkStrOutput(b_sub_set[1], 2, 1)
        self.assertSetEqual(set(b_sub_set[1].itervalues()), {'o1', 'o2', 'o3'})

        self.checkStrOutput(a, 1, 6)
        self.checkStrOutput(b, 2, 3)

    def testSetMethods(self):
        s = COWSetBase()
        with self.assertRaises(TypeError):
            print(s.iteritems())
        with self.assertRaises(TypeError):
            print(s.iterkeys())
