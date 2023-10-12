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
import os
import sys
import tempfile
import threading
import unittest
import socket
import time
import signal

def server_prefunc(server, idx):
    logging.basicConfig(level=logging.DEBUG, filename='bbhashserv-%d.log' % idx, filemode='w',
                        format='%(levelname)s %(filename)s:%(lineno)d %(message)s')
    server.logger.debug("Running server %d" % idx)
    sys.stdout = open('bbhashserv-stdout-%d.log' % idx, 'w')
    sys.stderr = sys.stdout

class HashEquivalenceTestSetup(object):
    METHOD = 'TestMethod'

    server_index = 0

    def start_server(self, dbpath=None, upstream=None, read_only=False, prefunc=server_prefunc):
        self.server_index += 1
        if dbpath is None:
            dbpath = os.path.join(self.temp_dir.name, "db%d.sqlite" % self.server_index)

        def cleanup_server(server):
            if server.process.exitcode is not None:
                return

            server.process.terminate()
            server.process.join()

        server = create_server(self.get_server_addr(self.server_index),
                               dbpath,
                               upstream=upstream,
                               read_only=read_only)
        server.dbpath = dbpath

        server.serve_as_process(prefunc=prefunc, args=(self.server_index,))
        self.addCleanup(cleanup_server, server)

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
        return taskhash, outhash, unihash

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

    def test_remove_taskhash(self):
        taskhash, outhash, unihash = self.test_create_hash()
        result = self.client.remove({"taskhash": taskhash})
        self.assertGreater(result["count"], 0)
        self.assertClientGetHash(self.client, taskhash, None)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_remove_unihash(self):
        taskhash, outhash, unihash = self.test_create_hash()
        result = self.client.remove({"unihash": unihash})
        self.assertGreater(result["count"], 0)
        self.assertClientGetHash(self.client, taskhash, None)

    def test_remove_outhash(self):
        taskhash, outhash, unihash = self.test_create_hash()
        result = self.client.remove({"outhash": outhash})
        self.assertGreater(result["count"], 0)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_remove_method(self):
        taskhash, outhash, unihash = self.test_create_hash()
        result = self.client.remove({"method": self.METHOD})
        self.assertGreater(result["count"], 0)
        self.assertClientGetHash(self.client, taskhash, None)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_clean_unused(self):
        taskhash, outhash, unihash = self.test_create_hash()

        # Clean the database, which should not remove anything because all hashes an in-use
        result = self.client.clean_unused(0)
        self.assertEqual(result["count"], 0)
        self.assertClientGetHash(self.client, taskhash, unihash)

        # Remove the unihash. The row in the outhash table should still be present
        self.client.remove({"unihash": unihash})
        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash, False)
        self.assertIsNotNone(result_outhash)

        # Now clean with no minimum age which will remove the outhash
        result = self.client.clean_unused(0)
        self.assertEqual(result["count"], 1)
        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash, False)
        self.assertIsNone(result_outhash)

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

        result_unihash = self.client.get_taskhash(self.METHOD, taskhash, True)
        self.assertEqual(result_unihash['taskhash'], taskhash)
        self.assertEqual(result_unihash['unihash'], unihash)
        self.assertEqual(result_unihash['method'], self.METHOD)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertEqual(result_outhash['taskhash'], taskhash)
        self.assertEqual(result_outhash['method'], self.METHOD)
        self.assertEqual(result_outhash['unihash'], unihash)
        self.assertEqual(result_outhash['outhash'], outhash)
        self.assertEqual(result_outhash['outhash_siginfo'], siginfo)

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

        # Tests read through from server with
        taskhash7 = '9d81d76242cc7cfaf7bf74b94b9cd2e29324ed74'
        outhash7 = '8470d56547eea6236d7c81a644ce74670ca0bbda998e13c629ef6bb3f0d60b69'
        unihash7 = '05d2a63c81e32f0a36542ca677e8ad852365c538'
        self.client.report_unihash(taskhash7, self.METHOD, outhash7, unihash7)

        result = down_client.get_taskhash(self.METHOD, taskhash7, True)
        self.assertEqual(result['unihash'], unihash7, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['outhash'], outhash7, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['taskhash'], taskhash7, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['method'], self.METHOD)

        taskhash8 = '86978a4c8c71b9b487330b0152aade10c1ee58aa'
        outhash8 = 'ca8c128e9d9e4a28ef24d0508aa20b5cf880604eacd8f65c0e366f7e0cc5fbcf'
        unihash8 = 'd8bcf25369d40590ad7d08c84d538982f2023e01'
        self.client.report_unihash(taskhash8, self.METHOD, outhash8, unihash8)

        result = down_client.get_outhash(self.METHOD, outhash8, taskhash8)
        self.assertEqual(result['unihash'], unihash8, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['outhash'], outhash8, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['taskhash'], taskhash8, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['method'], self.METHOD)

        taskhash9 = 'ae6339531895ddf5b67e663e6a374ad8ec71d81c'
        outhash9 = 'afc78172c81880ae10a1fec994b5b4ee33d196a001a1b66212a15ebe573e00b5'
        unihash9 = '6662e699d6e3d894b24408ff9a4031ef9b038ee8'
        self.client.report_unihash(taskhash9, self.METHOD, outhash9, unihash9)

        result = down_client.get_taskhash(self.METHOD, taskhash9, False)
        self.assertEqual(result['unihash'], unihash9, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['taskhash'], taskhash9, 'Server failed to copy unihash from upstream')
        self.assertEqual(result['method'], self.METHOD)

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

        with self.assertRaises(ConnectionError):
            ro_client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)

        # Ensure that the database was not modified
        self.assertClientGetHash(self.client, taskhash2, None)


    def test_slow_server_start(self):
        # Ensures that the server will exit correctly even if it gets a SIGTERM
        # before entering the main loop

        event = multiprocessing.Event()

        def prefunc(server, idx):
            nonlocal event
            server_prefunc(server, idx)
            event.wait()

        def do_nothing(signum, frame):
            pass

        old_signal = signal.signal(signal.SIGTERM, do_nothing)
        self.addCleanup(signal.signal, signal.SIGTERM, old_signal)

        _, server = self.start_server(prefunc=prefunc)
        server.process.terminate()
        time.sleep(30)
        event.set()
        server.process.join(300)
        self.assertIsNotNone(server.process.exitcode, "Server did not exit in a timely manner!")

    def test_diverging_report_race(self):
        # Tests that a reported task will correctly pick up an updated unihash

        # This is a baseline report added to the database to ensure that there
        # is something to match against as equivalent
        outhash1 = 'afd11c366050bcd75ad763e898e4430e2a60659b26f83fbb22201a60672019fa'
        taskhash1 = '3bde230c743fc45ab61a065d7a1815fbfa01c4740e4c895af2eb8dc0f684a4ab'
        unihash1 = '3bde230c743fc45ab61a065d7a1815fbfa01c4740e4c895af2eb8dc0f684a4ab'
        result = self.client.report_unihash(taskhash1, self.METHOD, outhash1, unihash1)

        # Add a report that is equivalent to Task 1. It should ignore the
        # provided unihash and report the unihash from task 1
        taskhash2 = '6259ae8263bd94d454c086f501c37e64c4e83cae806902ca95b4ab513546b273'
        unihash2 = taskhash2
        result = self.client.report_unihash(taskhash2, self.METHOD, outhash1, unihash2)
        self.assertEqual(result['unihash'], unihash1)

        # Add another report for Task 2, but with a different outhash (e.g. the
        # task is non-deterministic). It should still be marked with the Task 1
        # unihash because it has the Task 2 taskhash, which is equivalent to
        # Task 1
        outhash3 = 'd2187ee3a8966db10b34fe0e863482288d9a6185cb8ef58a6c1c6ace87a2f24c'
        result = self.client.report_unihash(taskhash2, self.METHOD, outhash3, unihash2)
        self.assertEqual(result['unihash'], unihash1)


    def test_diverging_report_reverse_race(self):
        # Same idea as the previous test, but Tasks 2 and 3 are reported in
        # reverse order the opposite order

        outhash1 = 'afd11c366050bcd75ad763e898e4430e2a60659b26f83fbb22201a60672019fa'
        taskhash1 = '3bde230c743fc45ab61a065d7a1815fbfa01c4740e4c895af2eb8dc0f684a4ab'
        unihash1 = '3bde230c743fc45ab61a065d7a1815fbfa01c4740e4c895af2eb8dc0f684a4ab'
        result = self.client.report_unihash(taskhash1, self.METHOD, outhash1, unihash1)

        taskhash2 = '6259ae8263bd94d454c086f501c37e64c4e83cae806902ca95b4ab513546b273'
        unihash2 = taskhash2

        # Report Task 3 first. Since there is nothing else in the database it
        # will use the client provided unihash
        outhash3 = 'd2187ee3a8966db10b34fe0e863482288d9a6185cb8ef58a6c1c6ace87a2f24c'
        result = self.client.report_unihash(taskhash2, self.METHOD, outhash3, unihash2)
        self.assertEqual(result['unihash'], unihash2)

        # Report Task 2. This is equivalent to Task 1 but there is already a mapping for
        # taskhash2 so it will report unihash2
        result = self.client.report_unihash(taskhash2, self.METHOD, outhash1, unihash2)
        self.assertEqual(result['unihash'], unihash2)

        # The originally reported unihash for Task 3 should be unchanged even if it
        # shares a taskhash with Task 2
        self.assertClientGetHash(self.client, taskhash2, unihash2)

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
