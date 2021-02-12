#! /usr/bin/env python3
#
# Copyright (C) 2018-2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

from . import create_server, create_client
from .client import HashConnectionError
import hashlib
import logging
import multiprocessing
import os
import sys
import tempfile
import threading
import unittest
import socket

def _run_server(server, idx):
    # logging.basicConfig(level=logging.DEBUG, filename='bbhashserv.log', filemode='w',
    #                     format='%(levelname)s %(filename)s:%(lineno)d %(message)s')
    sys.stdout = open('bbhashserv-%d.log' % idx, 'w')
    sys.stderr = sys.stdout
    server.serve_forever()


class HashEquivalenceTestSetup(object):
    METHOD = 'TestMethod'

    server_index = 0

    def start_server(self, dbpath=None, upstream=None, read_only=False):
        self.server_index += 1
        if dbpath is None:
            dbpath = os.path.join(self.temp_dir.name, "db%d.sqlite" % self.server_index)

        def cleanup_thread(thread):
            thread.terminate()
            thread.join()

        server = create_server(self.get_server_addr(self.server_index),
                               dbpath,
                               upstream=upstream,
                               read_only=read_only)
        server.dbpath = dbpath

        server.thread = multiprocessing.Process(target=_run_server, args=(server, self.server_index))
        server.thread.start()
        self.addCleanup(cleanup_thread, server.thread)

        def cleanup_client(client):
            client.close()

        client = create_client(server.address)
        self.addCleanup(cleanup_client, client)

        return (client, server)

    def setUp(self):
        if sys.version_info < (3, 5, 0):
            self.skipTest('Python 3.5 or later required')

        self.temp_dir = tempfile.TemporaryDirectory(prefix='bb-hashserv')
        self.addCleanup(self.temp_dir.cleanup)

        (self.client, self.server) = self.start_server()

    def assertClientGetHash(self, client, taskhash, unihash):
        result = client.get_unihash(self.METHOD, taskhash)
        self.assertEqual(result, unihash)


class HashEquivalenceCommonTests(object):
    def test_create_hash(self):
        # Simple test that hashes can be created
        taskhash = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash = 'f46d3fbb439bd9b921095da657a4de906510d2cd'

        self.assertClientGetHash(self.client, taskhash, None)

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

        self.assertClientGetHash(self.client, taskhash, unihash)

        outhash2 = '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d'
        unihash2 = 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'
        self.client.report_unihash(taskhash, self.METHOD, outhash2, unihash2)

        self.assertClientGetHash(self.client, taskhash, unihash)

        outhash3 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash3 = '9217a7d6398518e5dc002ed58f2cbbbc78696603'
        self.client.report_unihash(taskhash, self.METHOD, outhash3, unihash3)

        self.assertClientGetHash(self.client, taskhash, unihash)

    def test_huge_message(self):
        # Simple test that hashes can be created
        taskhash = 'c665584ee6817aa99edfc77a44dd853828279370'
        outhash = '3c979c3db45c569f51ab7626a4651074be3a9d11a84b1db076f5b14f7d39db44'
        unihash = '90e9bc1d1f094c51824adca7f8ea79a048d68824'

        self.assertClientGetHash(self.client, taskhash, None)

        siginfo = "0" * (self.client.max_chunk * 4)

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash, {
            'outhash_siginfo': siginfo
        })
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        result = self.client.get_taskhash(self.METHOD, taskhash, True)
        self.assertEqual(result['taskhash'], taskhash)
        self.assertEqual(result['unihash'], unihash)
        self.assertEqual(result['method'], self.METHOD)
        self.assertEqual(result['outhash'], outhash)
        self.assertEqual(result['outhash_siginfo'], siginfo)

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

    def test_upstream_server(self):
        # Tests upstream server support. This is done by creating two servers
        # that share a database file. The downstream server has it upstream
        # set to the test server, whereas the side server doesn't. This allows
        # verification that the hash requests are being proxied to the upstream
        # server by verifying that they appear on the downstream client, but not
        # the side client. It also verifies that the results are pulled into
        # the downstream database by checking that the downstream and side servers
        # match after the downstream is done waiting for all backfill tasks
        (down_client, down_server) = self.start_server(upstream=self.server.address)
        (side_client, side_server) = self.start_server(dbpath=down_server.dbpath)

        def check_hash(taskhash, unihash, old_sidehash):
            nonlocal down_client
            nonlocal side_client

            # check upstream server
            self.assertClientGetHash(self.client, taskhash, unihash)

            # Hash should *not* be present on the side server
            self.assertClientGetHash(side_client, taskhash, old_sidehash)

            # Hash should be present on the downstream server, since it
            # will defer to the upstream server. This will trigger
            # the backfill in the downstream server
            self.assertClientGetHash(down_client, taskhash, unihash)

            # After waiting for the downstream client to finish backfilling the
            # task from the upstream server, it should appear in the side server
            # since the database is populated
            down_client.backfill_wait()
            self.assertClientGetHash(side_client, taskhash, unihash)

        # Basic report
        taskhash = '8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a'
        outhash = 'afe240a439959ce86f5e322f8c208e1fedefea9e813f2140c81af866cc9edf7e'
        unihash = '218e57509998197d570e2c98512d0105985dffc9'
        self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)

        check_hash(taskhash, unihash, None)

        # Duplicated taskhash with multiple output hashes and unihashes.
        # All servers should agree with the originally reported hash
        outhash2 = '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d'
        unihash2 = 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'
        self.client.report_unihash(taskhash, self.METHOD, outhash2, unihash2)

        check_hash(taskhash, unihash, unihash)

        # Report an equivalent task. The sideload will originally report
        # no unihash until backfilled
        taskhash3 = "044c2ec8aaf480685a00ff6ff49e6162e6ad34e1"
        unihash3 = "def64766090d28f627e816454ed46894bb3aab36"
        self.client.report_unihash(taskhash3, self.METHOD, outhash, unihash3)

        check_hash(taskhash3, unihash, None)

        # Test that reporting a unihash in the downstream client isn't
        # propagating to the upstream server
        taskhash4 = "e3da00593d6a7fb435c7e2114976c59c5fd6d561"
        outhash4 = "1cf8713e645f491eb9c959d20b5cae1c47133a292626dda9b10709857cbe688a"
        unihash4 = "3b5d3d83f07f259e9086fcb422c855286e18a57d"
        down_client.report_unihash(taskhash4, self.METHOD, outhash4, unihash4)
        down_client.backfill_wait()

        self.assertClientGetHash(down_client, taskhash4, unihash4)
        self.assertClientGetHash(side_client, taskhash4, unihash4)
        self.assertClientGetHash(self.client, taskhash4, None)

        # Test that reporting a unihash in the downstream is able to find a
        # match which was previously reported to the upstream server
        taskhash5 = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash5 = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash5 = 'f46d3fbb439bd9b921095da657a4de906510d2cd'
        result = self.client.report_unihash(taskhash5, self.METHOD, outhash5, unihash5)

        taskhash6 = '35788efcb8dfb0a02659d81cf2bfd695fb30fafa'
        unihash6 = 'f46d3fbb439bd9b921095da657a4de906510d2ce'
        result = down_client.report_unihash(taskhash6, self.METHOD, outhash5, unihash6)
        self.assertEqual(result['unihash'], unihash5, 'Server failed to copy unihash from upstream')

    def test_ro_server(self):
        (ro_client, ro_server) = self.start_server(dbpath=self.server.dbpath, read_only=True)

        # Report a hash via the read-write server
        taskhash = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash = 'f46d3fbb439bd9b921095da657a4de906510d2cd'

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        # Check the hash via the read-only server
        self.assertClientGetHash(ro_client, taskhash, unihash)

        # Ensure that reporting via the read-only server fails
        taskhash2 = 'c665584ee6817aa99edfc77a44dd853828279370'
        outhash2 = '3c979c3db45c569f51ab7626a4651074be3a9d11a84b1db076f5b14f7d39db44'
        unihash2 = '90e9bc1d1f094c51824adca7f8ea79a048d68824'

        with self.assertRaises(HashConnectionError):
            ro_client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)

        # Ensure that the database was not modified
        self.assertClientGetHash(self.client, taskhash2, None)


class TestHashEquivalenceUnixServer(HashEquivalenceTestSetup, HashEquivalenceCommonTests, unittest.TestCase):
    def get_server_addr(self, server_idx):
        return "unix://" + os.path.join(self.temp_dir.name, 'sock%d' % server_idx)


class TestHashEquivalenceUnixServerLongPath(HashEquivalenceTestSetup, unittest.TestCase):
    DEEP_DIRECTORY = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb/ccccccccccccccccccccccccccccccccccccccccccc"
    def get_server_addr(self, server_idx):
        os.makedirs(os.path.join(self.temp_dir.name, self.DEEP_DIRECTORY), exist_ok=True)
        return "unix://" + os.path.join(self.temp_dir.name, self.DEEP_DIRECTORY, 'sock%d' % server_idx)


    def test_long_sock_path(self):
        # Simple test that hashes can be created
        taskhash = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash = 'f46d3fbb439bd9b921095da657a4de906510d2cd'

        self.assertClientGetHash(self.client, taskhash, None)

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')


class TestHashEquivalenceTCPServer(HashEquivalenceTestSetup, HashEquivalenceCommonTests, unittest.TestCase):
    def get_server_addr(self, server_idx):
        # Some hosts cause asyncio module to misbehave, when IPv6 is not enabled.
        # If IPv6 is enabled, it should be safe to use localhost directly, in general
        # case it is more reliable to resolve the IP address explicitly.
        return socket.gethostbyname("localhost") + ":0"
