#! /usr/bin/env python3
#
# Copyright (C) 2018-2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

from . import create_server, create_client
import hashlib
import logging
import multiprocessing
import sys
import tempfile
import threading
import unittest


class TestHashEquivalenceServer(object):
    METHOD = 'TestMethod'

    def _run_server(self):
        # logging.basicConfig(level=logging.DEBUG, filename='bbhashserv.log', filemode='w',
        #                     format='%(levelname)s %(filename)s:%(lineno)d %(message)s')
        self.server.serve_forever()

    def setUp(self):
        if sys.version_info < (3, 5, 0):
            self.skipTest('Python 3.5 or later required')

        self.temp_dir = tempfile.TemporaryDirectory(prefix='bb-hashserv')
        self.dbfile = os.path.join(self.temp_dir.name, 'db.sqlite')

        self.server = create_server(self.get_server_addr(), self.dbfile)
        self.server_thread = multiprocessing.Process(target=self._run_server)
        self.server_thread.start()
        self.client = create_client(self.server.address)

    def tearDown(self):
        # Shutdown server
        s = getattr(self, 'server', None)
        if s is not None:
            self.server_thread.terminate()
            self.server_thread.join()
        self.client.close()
        self.temp_dir.cleanup()

    def test_create_hash(self):
        # Simple test that hashes can be created
        taskhash = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash = 'f46d3fbb439bd9b921095da657a4de906510d2cd'

        result = self.client.get_unihash(self.METHOD, taskhash)
        self.assertIsNone(result, msg='Found unexpected task, %r' % result)

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

    def test_create_equivalent(self):
        # Tests that a second reported task with the same outhash will be
        # assigned the same unihash
        taskhash = '53b8dce672cb6d0c73170be43f540460bfc347b4'
        outhash = '5a9cb1649625f0bf41fc7791b635cd9c2d7118c7f021ba87dcd03f72b67ce7a8'
        unihash = 'f37918cc02eb5a520b1aff86faacbc0a38124646'

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        # Report a different task with the same outhash. The returned unihash
        # should match the first task
        taskhash2 = '3bf6f1e89d26205aec90da04854fbdbf73afe6b4'
        unihash2 = 'af36b199320e611fbb16f1f277d3ee1d619ca58b'
        result = self.client.report_unihash(taskhash2, self.METHOD, outhash, unihash2)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

    def test_duplicate_taskhash(self):
        # Tests that duplicate reports of the same taskhash with different
        # outhash & unihash always return the unihash from the first reported
        # taskhash
        taskhash = '8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a'
        outhash = 'afe240a439959ce86f5e322f8c208e1fedefea9e813f2140c81af866cc9edf7e'
        unihash = '218e57509998197d570e2c98512d0105985dffc9'
        self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)

        result = self.client.get_unihash(self.METHOD, taskhash)
        self.assertEqual(result, unihash)

        outhash2 = '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d'
        unihash2 = 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'
        self.client.report_unihash(taskhash, self.METHOD, outhash2, unihash2)

        result = self.client.get_unihash(self.METHOD, taskhash)
        self.assertEqual(result, unihash)

        outhash3 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash3 = '9217a7d6398518e5dc002ed58f2cbbbc78696603'
        self.client.report_unihash(taskhash, self.METHOD, outhash3, unihash3)

        result = self.client.get_unihash(self.METHOD, taskhash)
        self.assertEqual(result, unihash)

    def test_stress(self):
        def query_server(failures):
            client = Client(self.server.address)
            try:
                for i in range(1000):
                    taskhash = hashlib.sha256()
                    taskhash.update(str(i).encode('utf-8'))
                    taskhash = taskhash.hexdigest()
                    result = client.get_unihash(self.METHOD, taskhash)
                    if result != taskhash:
                        failures.append("taskhash mismatch: %s != %s" % (result, taskhash))
            finally:
                client.close()

        # Report hashes
        for i in range(1000):
            taskhash = hashlib.sha256()
            taskhash.update(str(i).encode('utf-8'))
            taskhash = taskhash.hexdigest()
            self.client.report_unihash(taskhash, self.METHOD, taskhash, taskhash)

        failures = []
        threads = [threading.Thread(target=query_server, args=(failures,)) for t in range(100)]

        for t in threads:
            t.start()

        for t in threads:
            t.join()

        self.assertFalse(failures)


class TestHashEquivalenceUnixServer(TestHashEquivalenceServer, unittest.TestCase):
    def get_server_addr(self):
        return "unix://" + os.path.join(self.temp_dir.name, 'sock')


class TestHashEquivalenceTCPServer(TestHashEquivalenceServer, unittest.TestCase):
    def get_server_addr(self):
        return "localhost:0"
