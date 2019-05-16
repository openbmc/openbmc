#
# BitBake Test for lib/bb/persist_data/
#
# Copyright (C) 2018 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import bb.data
import bb.persist_data
import tempfile
import threading

class PersistDataTest(unittest.TestCase):
    def _create_data(self):
        return bb.persist_data.persist('TEST_PERSIST_DATA', self.d)

    def setUp(self):
        self.d = bb.data.init()
        self.tempdir = tempfile.TemporaryDirectory()
        self.d['PERSISTENT_DIR'] = self.tempdir.name
        self.data = self._create_data()
        self.items = {
                'A1': '1',
                'B1': '2',
                'C2': '3'
                }
        self.stress_count = 10000
        self.thread_count = 5

        for k,v in self.items.items():
            self.data[k] = v

    def tearDown(self):
        self.tempdir.cleanup()

    def _iter_helper(self, seen, iterator):
        with iter(iterator):
            for v in iterator:
                self.assertTrue(v in seen)
                seen.remove(v)
        self.assertEqual(len(seen), 0, '%s not seen' % seen)

    def test_get(self):
        for k, v in self.items.items():
            self.assertEqual(self.data[k], v)

        self.assertIsNone(self.data.get('D'))
        with self.assertRaises(KeyError):
            self.data['D']

    def test_set(self):
        for k, v in self.items.items():
            self.data[k] += '-foo'

        for k, v in self.items.items():
            self.assertEqual(self.data[k], v + '-foo')

    def test_delete(self):
        self.data['D'] = '4'
        self.assertEqual(self.data['D'], '4')
        del self.data['D']
        self.assertIsNone(self.data.get('D'))
        with self.assertRaises(KeyError):
            self.data['D']

    def test_contains(self):
        for k in self.items:
            self.assertTrue(k in self.data)
            self.assertTrue(self.data.has_key(k))
        self.assertFalse('NotFound' in self.data)
        self.assertFalse(self.data.has_key('NotFound'))

    def test_len(self):
        self.assertEqual(len(self.data), len(self.items))

    def test_iter(self):
        self._iter_helper(set(self.items.keys()), self.data)

    def test_itervalues(self):
        self._iter_helper(set(self.items.values()), self.data.itervalues())

    def test_iteritems(self):
        self._iter_helper(set(self.items.items()), self.data.iteritems())

    def test_get_by_pattern(self):
        self._iter_helper({'1', '2'}, self.data.get_by_pattern('_1'))

    def _stress_read(self, data):
        for i in range(self.stress_count):
            for k in self.items:
                data[k]

    def _stress_write(self, data):
        for i in range(self.stress_count):
            for k, v in self.items.items():
                data[k] = v + str(i)

    def _validate_stress(self):
        for k, v in self.items.items():
            self.assertEqual(self.data[k], v + str(self.stress_count - 1))

    def test_stress(self):
        self._stress_read(self.data)
        self._stress_write(self.data)
        self._validate_stress()

    def test_stress_threads(self):
        def read_thread():
            data = self._create_data()
            self._stress_read(data)

        def write_thread():
            data = self._create_data()
            self._stress_write(data)

        threads = []
        for i in range(self.thread_count):
            threads.append(threading.Thread(target=read_thread))
            threads.append(threading.Thread(target=write_thread))

        for t in threads:
            t.start()
        self._stress_read(self.data)
        for t in threads:
            t.join()
        self._validate_stress()

