# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Tests for Copy-on-Write (cow.py)
#
# Copyright 2006 Holger Freyther <freyther@handhelds.org>
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

import unittest
import os

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

        self.assertEquals(False, a.has_key('a'))

        a['a'] = 'a'
        a['b'] = 'b'
        self.assertEquals(True, a.has_key('a'))
        self.assertEquals(True, a.has_key('b'))
        self.assertEquals('a', a['a'] )
        self.assertEquals('b', a['b'] )

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
        self.assertEquals(False, c.has_key('c'))
        self.assertEquals(30, c['a'])
        self.assertEquals(10, b['a'])

        # test copy
        b_2 = b.copy()
        c_2 = c.copy()

        self.assertEquals(False, c_2.has_key('c'))
        self.assertEquals(10, b_2['a'])

        b_2['d'] = 40
        self.assertEquals(False, c_2.has_key('d'))
        self.assertEquals(True, b_2.has_key('d'))
        self.assertEquals(40, b_2['d'])
        self.assertEquals(False, b.has_key('d'))
        self.assertEquals(False, c.has_key('d'))

        c_2['d'] = 30
        self.assertEquals(True, c_2.has_key('d'))
        self.assertEquals(True, b_2.has_key('d'))
        self.assertEquals(30, c_2['d'])
        self.assertEquals(40, b_2['d'])
        self.assertEquals(False, b.has_key('d'))
        self.assertEquals(False, c.has_key('d'))

        # test copy of the copy
        c_3 = c_2.copy()
        b_3 = b_2.copy()
        b_3_2 = b_2.copy()

        c_3['e'] = 4711
        self.assertEquals(4711, c_3['e'])
        self.assertEquals(False, c_2.has_key('e'))
        self.assertEquals(False, b_3.has_key('e'))
        self.assertEquals(False, b_3_2.has_key('e'))
        self.assertEquals(False, b_2.has_key('e'))

        b_3['e'] = 'viel'
        self.assertEquals('viel', b_3['e'])
        self.assertEquals(4711, c_3['e'])
        self.assertEquals(False, c_2.has_key('e'))
        self.assertEquals(True, b_3.has_key('e'))
        self.assertEquals(False, b_3_2.has_key('e'))
        self.assertEquals(False, b_2.has_key('e'))

    def testCow(self):
        from bb.COW import COWDictBase
        c = COWDictBase.copy()
        c['123'] = 1027
        c['other'] = 4711
        c['d'] = { 'abc' : 10, 'bcd' : 20 }

        copy = c.copy()

        self.assertEquals(1027, c['123'])
        self.assertEquals(4711, c['other'])
        self.assertEquals({'abc':10, 'bcd':20}, c['d'])
        self.assertEquals(1027, copy['123'])
        self.assertEquals(4711, copy['other'])
        self.assertEquals({'abc':10, 'bcd':20}, copy['d'])

        # cow it now
        copy['123'] = 1028
        copy['other'] = 4712
        copy['d']['abc'] = 20


        self.assertEquals(1027, c['123'])
        self.assertEquals(4711, c['other'])
        self.assertEquals({'abc':10, 'bcd':20}, c['d'])
        self.assertEquals(1028, copy['123'])
        self.assertEquals(4712, copy['other'])
        self.assertEquals({'abc':20, 'bcd':20}, copy['d'])
