#
# SPDX-License-Identifier: MIT
#

from unittest.case import TestCase
from oe.maketype import create

class TestBooleanType(TestCase):
    def test_invalid(self):
        self.assertRaises(ValueError, create, '', 'boolean')
        self.assertRaises(ValueError, create, 'foo', 'boolean')
        self.assertRaises(TypeError, create, object(), 'boolean')

    def test_true(self):
        self.assertTrue(create('y', 'boolean'))
        self.assertTrue(create('yes', 'boolean'))
        self.assertTrue(create('1', 'boolean'))
        self.assertTrue(create('t', 'boolean'))
        self.assertTrue(create('true', 'boolean'))
        self.assertTrue(create('TRUE', 'boolean'))
        self.assertTrue(create('truE', 'boolean'))

    def test_false(self):
        self.assertFalse(create('n', 'boolean'))
        self.assertFalse(create('no', 'boolean'))
        self.assertFalse(create('0', 'boolean'))
        self.assertFalse(create('f', 'boolean'))
        self.assertFalse(create('false', 'boolean'))
        self.assertFalse(create('FALSE', 'boolean'))
        self.assertFalse(create('faLse', 'boolean'))

    def test_bool_equality(self):
        self.assertEqual(create('n', 'boolean'), False)
        self.assertNotEqual(create('n', 'boolean'), True)
        self.assertEqual(create('y', 'boolean'), True)
        self.assertNotEqual(create('y', 'boolean'), False)

class TestList(TestCase):
    def assertListEqual(self, value, valid, sep=None):
        obj = create(value, 'list', separator=sep)
        self.assertEqual(obj, valid)
        if sep is not None:
            self.assertEqual(obj.separator, sep)
        self.assertEqual(str(obj), obj.separator.join(obj))

    def test_list_nosep(self):
        testlist = ['alpha', 'beta', 'theta']
        self.assertListEqual('alpha beta theta', testlist)
        self.assertListEqual('alpha  beta\ttheta', testlist)
        self.assertListEqual('alpha', ['alpha'])

    def test_list_usersep(self):
        self.assertListEqual('foo:bar', ['foo', 'bar'], ':')
        self.assertListEqual('foo:bar:baz', ['foo', 'bar', 'baz'], ':')
