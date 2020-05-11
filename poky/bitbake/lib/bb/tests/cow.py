#
# BitBake Tests for Copy-on-Write (cow.py)
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Copyright 2006 Holger Freyther <freyther@handhelds.org>
#

import unittest


class COWTestCase(unittest.TestCase):
    """
    Test case for the COW module from mithro
    """

    def testGetSet(self):
        """
        Test and set
        """
        from bb.COW import COWDictBase
        a = COWDictBase.copy()

        self.assertEqual(False, 'a' in a)

        a['a'] = 'a'
        a['b'] = 'b'
        self.assertEqual(True, 'a' in a)
        self.assertEqual(True, 'b' in a)
        self.assertEqual('a', a['a'] )
        self.assertEqual('b', a['b'] )

    def testCopyCopy(self):
        """
        Test the copy of copies
        """

        from bb.COW import COWDictBase

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
        from bb.COW import COWDictBase
        c = COWDictBase.copy()
        c['123'] = 1027
        c['other'] = 4711
        c['d'] = { 'abc' : 10, 'bcd' : 20 }

        copy = c.copy()

        self.assertEqual(1027, c['123'])
        self.assertEqual(4711, c['other'])
        self.assertEqual({'abc':10, 'bcd':20}, c['d'])
        self.assertEqual(1027, copy['123'])
        self.assertEqual(4711, copy['other'])
        self.assertEqual({'abc':10, 'bcd':20}, copy['d'])

        # cow it now
        copy['123'] = 1028
        copy['other'] = 4712
        copy['d']['abc'] = 20


        self.assertEqual(1027, c['123'])
        self.assertEqual(4711, c['other'])
        self.assertEqual({'abc':10, 'bcd':20}, c['d'])
        self.assertEqual(1028, copy['123'])
        self.assertEqual(4712, copy['other'])
        self.assertEqual({'abc':20, 'bcd':20}, copy['d'])
