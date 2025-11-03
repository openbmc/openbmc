#! /usr/bin/env python3
#
# Copyright (C) 2018-2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

from . import create_server, create_client
from .server import DEFAULT_ANON_PERMS, ALL_PERMISSIONS
from bb.asyncrpc import InvokeError
from .client import ClientPool
import hashlib
import logging
from bb import multiprocessing
import os
import sys
import tempfile
import threading
import unittest
import socket
import time
import signal
import subprocess
import json
import re
from pathlib import Path


THIS_DIR = Path(__file__).parent
BIN_DIR = THIS_DIR.parent.parent / "bin"

def server_prefunc(server, idx):
    logging.basicConfig(level=logging.DEBUG, filename='bbhashserv-%d.log' % idx, filemode='w',
                        format='%(levelname)s %(filename)s:%(lineno)d %(message)s')
    server.logger.debug("Running server %d" % idx)
    sys.stdout = open('bbhashserv-stdout-%d.log' % idx, 'w')
    sys.stderr = sys.stdout

class HashEquivalenceTestSetup(object):
    METHOD = 'TestMethod'

    server_index = 0
    client_index = 0

    def start_server(self, dbpath=None, upstream=None, read_only=False, prefunc=server_prefunc, anon_perms=DEFAULT_ANON_PERMS, admin_username=None, admin_password=None):
        self.server_index += 1
        if dbpath is None:
            dbpath = self.make_dbpath()

        def cleanup_server(server):
            if server.process.exitcode is not None:
                return

            server.process.terminate()
            server.process.join()

        server = create_server(self.get_server_addr(self.server_index),
                               dbpath,
                               upstream=upstream,
                               read_only=read_only,
                               anon_perms=anon_perms,
                               admin_username=admin_username,
                               admin_password=admin_password)
        server.dbpath = dbpath

        server.serve_as_process(prefunc=prefunc, args=(self.server_index,))
        self.addCleanup(cleanup_server, server)

        return server

    def make_dbpath(self):
        return os.path.join(self.temp_dir.name, "db%d.sqlite" % self.server_index)

    def start_client(self, server_address, username=None, password=None):
        def cleanup_client(client):
            client.close()

        client = create_client(server_address, username=username, password=password)
        self.addCleanup(cleanup_client, client)

        return client

    def start_test_server(self):
        self.server = self.start_server()
        return self.server.address

    def start_auth_server(self):
        auth_server = self.start_server(self.server.dbpath, anon_perms=[], admin_username="admin", admin_password="password")
        self.auth_server_address = auth_server.address
        self.admin_client = self.start_client(auth_server.address, username="admin", password="password")
        return self.admin_client

    def auth_client(self, user):
        return self.start_client(self.auth_server_address, user["username"], user["token"])

    def setUp(self):
        if sys.version_info < (3, 5, 0):
            self.skipTest('Python 3.5 or later required')

        self.temp_dir = tempfile.TemporaryDirectory(prefix='bb-hashserv')
        self.addCleanup(self.temp_dir.cleanup)

        self.server_address = self.start_test_server()

        self.client = self.start_client(self.server_address)

    def assertClientGetHash(self, client, taskhash, unihash):
        result = client.get_unihash(self.METHOD, taskhash)
        self.assertEqual(result, unihash)

    def assertUserPerms(self, user, permissions):
        with self.auth_client(user) as client:
            info = client.get_user()
            self.assertEqual(info, {
                "username": user["username"],
                "permissions": permissions,
            })

    def assertUserCanAuth(self, user):
        with self.start_client(self.auth_server_address) as client:
            client.auth(user["username"], user["token"])

    def assertUserCannotAuth(self, user):
        with self.start_client(self.auth_server_address) as client, self.assertRaises(InvokeError):
            client.auth(user["username"], user["token"])

    def create_test_hash(self, client):
        # Simple test that hashes can be created
        taskhash = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash = 'f46d3fbb439bd9b921095da657a4de906510d2cd'

        self.assertClientGetHash(client, taskhash, None)

        result = client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')
        return taskhash, outhash, unihash

    def run_hashclient(self, args, **kwargs):
        try:
            p = subprocess.run(
                [BIN_DIR / "bitbake-hashclient"] + args,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                encoding="utf-8",
                **kwargs
            )
        except subprocess.CalledProcessError as e:
            print(e.output)
            raise e

        print(p.stdout)
        return p


class HashEquivalenceCommonTests(object):
    def auth_perms(self, *permissions):
        self.client_index += 1
        user = self.create_user(f"user-{self.client_index}", permissions)
        return self.auth_client(user)

    def create_user(self, username, permissions, *, client=None):
        def remove_user(username):
            try:
                self.admin_client.delete_user(username)
            except bb.asyncrpc.InvokeError:
                pass

        if client is None:
            client = self.admin_client

        user = client.new_user(username, permissions)
        self.addCleanup(remove_user, username)

        return user

    def test_create_hash(self):
        return self.create_test_hash(self.client)

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
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        result = self.client.remove({"taskhash": taskhash})
        self.assertGreater(result["count"], 0)
        self.assertClientGetHash(self.client, taskhash, None)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_remove_unihash(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        result = self.client.remove({"unihash": unihash})
        self.assertGreater(result["count"], 0)
        self.assertClientGetHash(self.client, taskhash, None)

    def test_remove_outhash(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        result = self.client.remove({"outhash": outhash})
        self.assertGreater(result["count"], 0)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_remove_method(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        result = self.client.remove({"method": self.METHOD})
        self.assertGreater(result["count"], 0)
        self.assertClientGetHash(self.client, taskhash, None)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_clean_unused(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)

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
            client = Client(self.server_address)
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
        down_server = self.start_server(upstream=self.server_address)
        down_client = self.start_client(down_server.address)
        side_server = self.start_server(dbpath=down_server.dbpath)
        side_client = self.start_client(side_server.address)

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

    def test_unihash_exsits(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        self.assertTrue(self.client.unihash_exists(unihash))
        self.assertFalse(self.client.unihash_exists('6662e699d6e3d894b24408ff9a4031ef9b038ee8'))

    def test_ro_server(self):
        rw_server = self.start_server()
        rw_client = self.start_client(rw_server.address)

        ro_server = self.start_server(dbpath=rw_server.dbpath, read_only=True)
        ro_client = self.start_client(ro_server.address)

        # Report a hash via the read-write server
        taskhash = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash = 'f46d3fbb439bd9b921095da657a4de906510d2cd'

        result = rw_client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        # Check the hash via the read-only server
        self.assertClientGetHash(ro_client, taskhash, unihash)

        # Ensure that reporting via the read-only server fails
        taskhash2 = 'c665584ee6817aa99edfc77a44dd853828279370'
        outhash2 = '3c979c3db45c569f51ab7626a4651074be3a9d11a84b1db076f5b14f7d39db44'
        unihash2 = '90e9bc1d1f094c51824adca7f8ea79a048d68824'

        result = ro_client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)
        self.assertEqual(result['unihash'], unihash2)

        # Ensure that the database was not modified
        self.assertClientGetHash(rw_client, taskhash2, None)


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

        server = self.start_server(prefunc=prefunc)
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


    def test_client_pool_get_unihashes(self):
        TEST_INPUT = (
            # taskhash                                   outhash                                                            unihash
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', 'afe240a439959ce86f5e322f8c208e1fedefea9e813f2140c81af866cc9edf7e','218e57509998197d570e2c98512d0105985dffc9'),
            # Duplicated taskhash with multiple output hashes and unihashes.
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'),
            # Equivalent hash
            ("044c2ec8aaf480685a00ff6ff49e6162e6ad34e1", '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', "def64766090d28f627e816454ed46894bb3aab36"),
            ("e3da00593d6a7fb435c7e2114976c59c5fd6d561", "1cf8713e645f491eb9c959d20b5cae1c47133a292626dda9b10709857cbe688a", "3b5d3d83f07f259e9086fcb422c855286e18a57d"),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30faf9', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2cd'),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30fafa', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2ce'),
            ('9d81d76242cc7cfaf7bf74b94b9cd2e29324ed74', '8470d56547eea6236d7c81a644ce74670ca0bbda998e13c629ef6bb3f0d60b69', '05d2a63c81e32f0a36542ca677e8ad852365c538'),
        )
        EXTRA_QUERIES = (
            "6b6be7a84ab179b4240c4302518dc3f6",
        )

        with ClientPool(self.server_address, 10) as client_pool:
            for taskhash, outhash, unihash in TEST_INPUT:
                self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)

            query = {idx: (self.METHOD, data[0]) for idx, data in enumerate(TEST_INPUT)}
            for idx, taskhash in enumerate(EXTRA_QUERIES):
                query[idx + len(TEST_INPUT)] = (self.METHOD, taskhash)

            result = client_pool.get_unihashes(query)

            self.assertDictEqual(result, {
                0: "218e57509998197d570e2c98512d0105985dffc9",
                1: "218e57509998197d570e2c98512d0105985dffc9",
                2: "218e57509998197d570e2c98512d0105985dffc9",
                3: "3b5d3d83f07f259e9086fcb422c855286e18a57d",
                4: "f46d3fbb439bd9b921095da657a4de906510d2cd",
                5: "f46d3fbb439bd9b921095da657a4de906510d2cd",
                6: "05d2a63c81e32f0a36542ca677e8ad852365c538",
                7: None,
            })

    def test_get_unihash_batch(self):
        TEST_INPUT = (
            # taskhash                                   outhash                                                            unihash
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', 'afe240a439959ce86f5e322f8c208e1fedefea9e813f2140c81af866cc9edf7e','218e57509998197d570e2c98512d0105985dffc9'),
            # Duplicated taskhash with multiple output hashes and unihashes.
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'),
            # Equivalent hash
            ("044c2ec8aaf480685a00ff6ff49e6162e6ad34e1", '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', "def64766090d28f627e816454ed46894bb3aab36"),
            ("e3da00593d6a7fb435c7e2114976c59c5fd6d561", "1cf8713e645f491eb9c959d20b5cae1c47133a292626dda9b10709857cbe688a", "3b5d3d83f07f259e9086fcb422c855286e18a57d"),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30faf9', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2cd'),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30fafa', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2ce'),
            ('9d81d76242cc7cfaf7bf74b94b9cd2e29324ed74', '8470d56547eea6236d7c81a644ce74670ca0bbda998e13c629ef6bb3f0d60b69', '05d2a63c81e32f0a36542ca677e8ad852365c538'),
        )
        EXTRA_QUERIES = (
            "6b6be7a84ab179b4240c4302518dc3f6",
        )

        for taskhash, outhash, unihash in TEST_INPUT:
            self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)


        result = self.client.get_unihash_batch(
            [(self.METHOD, data[0]) for data in TEST_INPUT] +
            [(self.METHOD, e) for e in EXTRA_QUERIES]
        )

        self.assertListEqual(result, [
            "218e57509998197d570e2c98512d0105985dffc9",
            "218e57509998197d570e2c98512d0105985dffc9",
            "218e57509998197d570e2c98512d0105985dffc9",
            "3b5d3d83f07f259e9086fcb422c855286e18a57d",
            "f46d3fbb439bd9b921095da657a4de906510d2cd",
            "f46d3fbb439bd9b921095da657a4de906510d2cd",
            "05d2a63c81e32f0a36542ca677e8ad852365c538",
            None,
        ])

    def test_client_pool_unihash_exists(self):
        TEST_INPUT = (
            # taskhash                                   outhash                                                            unihash
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', 'afe240a439959ce86f5e322f8c208e1fedefea9e813f2140c81af866cc9edf7e','218e57509998197d570e2c98512d0105985dffc9'),
            # Duplicated taskhash with multiple output hashes and unihashes.
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'),
            # Equivalent hash
            ("044c2ec8aaf480685a00ff6ff49e6162e6ad34e1", '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', "def64766090d28f627e816454ed46894bb3aab36"),
            ("e3da00593d6a7fb435c7e2114976c59c5fd6d561", "1cf8713e645f491eb9c959d20b5cae1c47133a292626dda9b10709857cbe688a", "3b5d3d83f07f259e9086fcb422c855286e18a57d"),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30faf9', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2cd'),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30fafa', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2ce'),
            ('9d81d76242cc7cfaf7bf74b94b9cd2e29324ed74', '8470d56547eea6236d7c81a644ce74670ca0bbda998e13c629ef6bb3f0d60b69', '05d2a63c81e32f0a36542ca677e8ad852365c538'),
        )
        EXTRA_QUERIES = (
            "6b6be7a84ab179b4240c4302518dc3f6",
        )

        result_unihashes = set()


        with ClientPool(self.server_address, 10) as client_pool:
            for taskhash, outhash, unihash in TEST_INPUT:
                result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
                result_unihashes.add(result["unihash"])

            query = {}
            expected = {}

            for _, _, unihash in TEST_INPUT:
                idx = len(query)
                query[idx] = unihash
                expected[idx] = unihash in result_unihashes


            for unihash in EXTRA_QUERIES:
                idx = len(query)
                query[idx] = unihash
                expected[idx] = False

            result = client_pool.unihashes_exist(query)
            self.assertDictEqual(result, expected)

    def test_unihash_exists_batch(self):
        TEST_INPUT = (
            # taskhash                                   outhash                                                            unihash
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', 'afe240a439959ce86f5e322f8c208e1fedefea9e813f2140c81af866cc9edf7e','218e57509998197d570e2c98512d0105985dffc9'),
            # Duplicated taskhash with multiple output hashes and unihashes.
            ('8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a', '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'),
            # Equivalent hash
            ("044c2ec8aaf480685a00ff6ff49e6162e6ad34e1", '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d', "def64766090d28f627e816454ed46894bb3aab36"),
            ("e3da00593d6a7fb435c7e2114976c59c5fd6d561", "1cf8713e645f491eb9c959d20b5cae1c47133a292626dda9b10709857cbe688a", "3b5d3d83f07f259e9086fcb422c855286e18a57d"),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30faf9', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2cd'),
            ('35788efcb8dfb0a02659d81cf2bfd695fb30fafa', '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f', 'f46d3fbb439bd9b921095da657a4de906510d2ce'),
            ('9d81d76242cc7cfaf7bf74b94b9cd2e29324ed74', '8470d56547eea6236d7c81a644ce74670ca0bbda998e13c629ef6bb3f0d60b69', '05d2a63c81e32f0a36542ca677e8ad852365c538'),
        )
        EXTRA_QUERIES = (
            "6b6be7a84ab179b4240c4302518dc3f6",
        )

        result_unihashes = set()


        for taskhash, outhash, unihash in TEST_INPUT:
            result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
            result_unihashes.add(result["unihash"])

        query = []
        expected = []

        for _, _, unihash in TEST_INPUT:
            query.append(unihash)
            expected.append(unihash in result_unihashes)


        for unihash in EXTRA_QUERIES:
            query.append(unihash)
            expected.append(False)

        result = self.client.unihash_exists_batch(query)
        self.assertListEqual(result, expected)

    def test_auth_read_perms(self):
        admin_client = self.start_auth_server()

        # Create hashes with non-authenticated server
        taskhash, outhash, unihash = self.create_test_hash(self.client)

        # Validate hash can be retrieved using authenticated client
        with self.auth_perms("@read") as client:
            self.assertClientGetHash(client, taskhash, unihash)

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            self.assertClientGetHash(client, taskhash, unihash)

    def test_auth_report_perms(self):
        admin_client = self.start_auth_server()

        # Without read permission, the user is completely denied
        with self.auth_perms() as client, self.assertRaises(InvokeError):
            self.create_test_hash(client)

        # Read permission allows the call to succeed, but it doesn't record
        # anythin in the database
        with self.auth_perms("@read") as client:
            taskhash, outhash, unihash = self.create_test_hash(client)
            self.assertClientGetHash(client, taskhash, None)

        # Report permission alone is insufficient
        with self.auth_perms("@report") as client, self.assertRaises(InvokeError):
            self.create_test_hash(client)

        # Read and report permission actually modify the database
        with self.auth_perms("@read", "@report") as client:
            taskhash, outhash, unihash = self.create_test_hash(client)
            self.assertClientGetHash(client, taskhash, unihash)

    def test_auth_no_token_refresh_from_anon_user(self):
        self.start_auth_server()

        with self.start_client(self.auth_server_address) as client, self.assertRaises(InvokeError):
            client.refresh_token()

    def test_auth_self_token_refresh(self):
        admin_client = self.start_auth_server()

        # Create a new user with no permissions
        user = self.create_user("test-user", [])

        with self.auth_client(user) as client:
            new_user = client.refresh_token()

        self.assertEqual(user["username"], new_user["username"])
        self.assertNotEqual(user["token"], new_user["token"])
        self.assertUserCanAuth(new_user)
        self.assertUserCannotAuth(user)

        # Explicitly specifying with your own username is fine also
        with self.auth_client(new_user) as client:
            new_user2 = client.refresh_token(user["username"])

        self.assertEqual(user["username"], new_user2["username"])
        self.assertNotEqual(user["token"], new_user2["token"])
        self.assertUserCanAuth(new_user2)
        self.assertUserCannotAuth(new_user)
        self.assertUserCannotAuth(user)

    def test_auth_token_refresh(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", [])

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.refresh_token(user["username"])

        with self.auth_perms("@user-admin") as client:
            new_user = client.refresh_token(user["username"])

        self.assertEqual(user["username"], new_user["username"])
        self.assertNotEqual(user["token"], new_user["token"])
        self.assertUserCanAuth(new_user)
        self.assertUserCannotAuth(user)

    def test_auth_self_get_user(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", [])
        user_info = user.copy()
        del user_info["token"]

        with self.auth_client(user) as client:
            info = client.get_user()
            self.assertEqual(info, user_info)

            # Explicitly asking for your own username is fine also
            info = client.get_user(user["username"])
            self.assertEqual(info, user_info)

    def test_auth_get_user(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", [])
        user_info = user.copy()
        del user_info["token"]

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.get_user(user["username"])

        with self.auth_perms("@user-admin") as client:
            info = client.get_user(user["username"])
            self.assertEqual(info, user_info)

            info = client.get_user("nonexist-user")
            self.assertIsNone(info)

    def test_auth_reconnect(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", [])
        user_info = user.copy()
        del user_info["token"]

        with self.auth_client(user) as client:
            info = client.get_user()
            self.assertEqual(info, user_info)

            client.disconnect()

            info = client.get_user()
            self.assertEqual(info, user_info)

    def test_auth_delete_user(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", [])

        # self service
        with self.auth_client(user) as client:
            client.delete_user(user["username"])

        self.assertIsNone(admin_client.get_user(user["username"]))
        user = self.create_user("test-user", [])

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.delete_user(user["username"])

        with self.auth_perms("@user-admin") as client:
            client.delete_user(user["username"])

        # User doesn't exist, so even though the permission is correct, it's an
        # error
        with self.auth_perms("@user-admin") as client, self.assertRaises(InvokeError):
            client.delete_user(user["username"])

    def test_auth_set_user_perms(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", [])

        self.assertUserPerms(user, [])

        # No self service to change permissions
        with self.auth_client(user) as client, self.assertRaises(InvokeError):
            client.set_user_perms(user["username"], ["@all"])
        self.assertUserPerms(user, [])

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.set_user_perms(user["username"], ["@all"])
        self.assertUserPerms(user, [])

        with self.auth_perms("@user-admin") as client:
            client.set_user_perms(user["username"], ["@all"])
        self.assertUserPerms(user, sorted(list(ALL_PERMISSIONS)))

        # Bad permissions
        with self.auth_perms("@user-admin") as client, self.assertRaises(InvokeError):
            client.set_user_perms(user["username"], ["@this-is-not-a-permission"])
        self.assertUserPerms(user, sorted(list(ALL_PERMISSIONS)))

    def test_auth_get_all_users(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", [])

        with self.auth_client(user) as client, self.assertRaises(InvokeError):
            client.get_all_users()

        # Give the test user the correct permission
        admin_client.set_user_perms(user["username"], ["@user-admin"])

        with self.auth_client(user) as client:
            all_users = client.get_all_users()

        # Convert to a dictionary for easier comparison
        all_users = {u["username"]: u for u in all_users}

        self.assertEqual(all_users,
            {
                "admin": {
                    "username": "admin",
                    "permissions": sorted(list(ALL_PERMISSIONS)),
                },
                "test-user": {
                    "username": "test-user",
                    "permissions": ["@user-admin"],
                }
            }
        )

    def test_auth_new_user(self):
        self.start_auth_server()

        permissions = ["@read", "@report", "@db-admin", "@user-admin"]
        permissions.sort()

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            self.create_user("test-user", permissions, client=client)

        with self.auth_perms("@user-admin") as client:
            user = self.create_user("test-user", permissions, client=client)
            self.assertIn("token", user)
            self.assertEqual(user["username"], "test-user")
            self.assertEqual(user["permissions"], permissions)

    def test_auth_become_user(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", ["@read", "@report"])
        user_info = user.copy()
        del user_info["token"]

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.become_user(user["username"])

        with self.auth_perms("@user-admin") as client:
            become = client.become_user(user["username"])
            self.assertEqual(become, user_info)

            info = client.get_user()
            self.assertEqual(info, user_info)

            # Verify become user is preserved across disconnect
            client.disconnect()

            info = client.get_user()
            self.assertEqual(info, user_info)

            # test-user doesn't have become_user permissions, so this should
            # not work
            with self.assertRaises(InvokeError):
                client.become_user(user["username"])

        # No self-service of become
        with self.auth_client(user) as client, self.assertRaises(InvokeError):
            client.become_user(user["username"])

        # Give test user permissions to become
        admin_client.set_user_perms(user["username"], ["@user-admin"])

        # It's possible to become yourself (effectively a noop)
        with self.auth_perms("@user-admin") as client:
            become = client.become_user(client.username)

    def test_auth_gc(self):
        admin_client = self.start_auth_server()

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.gc_mark("ABC", {"unihash": "123"})

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.gc_status()

        with self.auth_perms() as client, self.assertRaises(InvokeError):
            client.gc_sweep("ABC")

        with self.auth_perms("@db-admin") as client:
            client.gc_mark("ABC", {"unihash": "123"})

        with self.auth_perms("@db-admin") as client:
            client.gc_status()

        with self.auth_perms("@db-admin") as client:
            client.gc_sweep("ABC")

    def test_get_db_usage(self):
        usage = self.client.get_db_usage()

        self.assertTrue(isinstance(usage, dict))
        for name in usage.keys():
            self.assertTrue(isinstance(usage[name], dict))
            self.assertIn("rows", usage[name])
            self.assertTrue(isinstance(usage[name]["rows"], int))

    def test_get_db_query_columns(self):
        columns = self.client.get_db_query_columns()

        self.assertTrue(isinstance(columns, list))
        self.assertTrue(len(columns) > 0)

        for col in columns:
            self.client.remove({col: ""})

    def test_auth_is_owner(self):
        admin_client = self.start_auth_server()

        user = self.create_user("test-user", ["@read", "@report"])
        with self.auth_client(user) as client:
            taskhash, outhash, unihash = self.create_test_hash(client)
            data = client.get_taskhash(self.METHOD, taskhash, True)
            self.assertEqual(data["owner"], user["username"])

    def test_gc(self):
        taskhash = '53b8dce672cb6d0c73170be43f540460bfc347b4'
        outhash = '5a9cb1649625f0bf41fc7791b635cd9c2d7118c7f021ba87dcd03f72b67ce7a8'
        unihash = 'f37918cc02eb5a520b1aff86faacbc0a38124646'

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        taskhash2 = '3bf6f1e89d26205aec90da04854fbdbf73afe6b4'
        outhash2 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash2 = 'af36b199320e611fbb16f1f277d3ee1d619ca58b'

        result = self.client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        # Mark the first unihash to be kept
        ret = self.client.gc_mark("ABC", {"unihash": unihash, "method": self.METHOD})
        self.assertEqual(ret, {"count": 1})

        ret = self.client.gc_status()
        self.assertEqual(ret, {"mark": "ABC", "keep": 1, "remove": 1})

        # Second hash is still there; mark doesn't delete hashes
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        ret = self.client.gc_sweep("ABC")
        self.assertEqual(ret, {"count": 1})

        # Hash is gone. Taskhash is returned for second hash
        self.assertClientGetHash(self.client, taskhash2, None)
        # First hash is still present
        self.assertClientGetHash(self.client, taskhash, unihash)

    def test_gc_switch_mark(self):
        taskhash = '53b8dce672cb6d0c73170be43f540460bfc347b4'
        outhash = '5a9cb1649625f0bf41fc7791b635cd9c2d7118c7f021ba87dcd03f72b67ce7a8'
        unihash = 'f37918cc02eb5a520b1aff86faacbc0a38124646'

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        taskhash2 = '3bf6f1e89d26205aec90da04854fbdbf73afe6b4'
        outhash2 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash2 = 'af36b199320e611fbb16f1f277d3ee1d619ca58b'

        result = self.client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        # Mark the first unihash to be kept
        ret = self.client.gc_mark("ABC", {"unihash": unihash, "method": self.METHOD})
        self.assertEqual(ret, {"count": 1})

        ret = self.client.gc_status()
        self.assertEqual(ret, {"mark": "ABC", "keep": 1, "remove": 1})

        # Second hash is still there; mark doesn't delete hashes
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        # Switch to a different mark and mark the second hash. This will start
        # a new collection cycle
        ret = self.client.gc_mark("DEF", {"unihash": unihash2, "method": self.METHOD})
        self.assertEqual(ret, {"count": 1})

        ret = self.client.gc_status()
        self.assertEqual(ret, {"mark": "DEF", "keep": 1, "remove": 1})

        # Both hashes are still present
        self.assertClientGetHash(self.client, taskhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash, unihash)

        # Sweep with the new mark
        ret = self.client.gc_sweep("DEF")
        self.assertEqual(ret, {"count": 1})

        # First hash is gone, second is kept
        self.assertClientGetHash(self.client, taskhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash, None)

    def test_gc_switch_sweep_mark(self):
        taskhash = '53b8dce672cb6d0c73170be43f540460bfc347b4'
        outhash = '5a9cb1649625f0bf41fc7791b635cd9c2d7118c7f021ba87dcd03f72b67ce7a8'
        unihash = 'f37918cc02eb5a520b1aff86faacbc0a38124646'

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        taskhash2 = '3bf6f1e89d26205aec90da04854fbdbf73afe6b4'
        outhash2 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash2 = 'af36b199320e611fbb16f1f277d3ee1d619ca58b'

        result = self.client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        # Mark the first unihash to be kept
        ret = self.client.gc_mark("ABC", {"unihash": unihash, "method": self.METHOD})
        self.assertEqual(ret, {"count": 1})

        ret = self.client.gc_status()
        self.assertEqual(ret, {"mark": "ABC", "keep": 1, "remove": 1})

        # Sweeping with a different mark raises an error
        with self.assertRaises(InvokeError):
            self.client.gc_sweep("DEF")

        # Both hashes are present
        self.assertClientGetHash(self.client, taskhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash, unihash)

    def test_gc_new_hashes(self):
        taskhash = '53b8dce672cb6d0c73170be43f540460bfc347b4'
        outhash = '5a9cb1649625f0bf41fc7791b635cd9c2d7118c7f021ba87dcd03f72b67ce7a8'
        unihash = 'f37918cc02eb5a520b1aff86faacbc0a38124646'

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        # Start a new garbage collection
        ret = self.client.gc_mark("ABC", {"unihash": unihash, "method": self.METHOD})
        self.assertEqual(ret, {"count": 1})

        ret = self.client.gc_status()
        self.assertEqual(ret, {"mark": "ABC", "keep": 1, "remove": 0})

        # Add second hash. It should inherit the mark from the current garbage
        # collection operation

        taskhash2 = '3bf6f1e89d26205aec90da04854fbdbf73afe6b4'
        outhash2 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash2 = 'af36b199320e611fbb16f1f277d3ee1d619ca58b'

        result = self.client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        # Sweep should remove nothing
        ret = self.client.gc_sweep("ABC")
        self.assertEqual(ret, {"count": 0})

        # Both hashes are present
        self.assertClientGetHash(self.client, taskhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash, unihash)


class TestHashEquivalenceClient(HashEquivalenceTestSetup, unittest.TestCase):
    def get_server_addr(self, server_idx):
        return "unix://" + os.path.join(self.temp_dir.name, 'sock%d' % server_idx)

    def test_get(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)

        p = self.run_hashclient(["--address", self.server_address, "get", self.METHOD, taskhash])
        data = json.loads(p.stdout)
        self.assertEqual(data["unihash"], unihash)
        self.assertEqual(data["outhash"], outhash)
        self.assertEqual(data["taskhash"], taskhash)
        self.assertEqual(data["method"], self.METHOD)

    def test_get_outhash(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)

        p = self.run_hashclient(["--address", self.server_address, "get-outhash", self.METHOD, outhash, taskhash])
        data = json.loads(p.stdout)
        self.assertEqual(data["unihash"], unihash)
        self.assertEqual(data["outhash"], outhash)
        self.assertEqual(data["taskhash"], taskhash)
        self.assertEqual(data["method"], self.METHOD)

    def test_stats(self):
        p = self.run_hashclient(["--address", self.server_address, "stats"], check=True)
        json.loads(p.stdout)

    def test_stress(self):
        self.run_hashclient(["--address", self.server_address, "stress"], check=True)

    def test_unihash_exsits(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)

        p = self.run_hashclient([
            "--address", self.server_address,
            "unihash-exists", unihash,
        ], check=True)
        self.assertEqual(p.stdout.strip(), "true")

        p = self.run_hashclient([
            "--address", self.server_address,
            "unihash-exists", '6662e699d6e3d894b24408ff9a4031ef9b038ee8',
        ], check=True)
        self.assertEqual(p.stdout.strip(), "false")

    def test_unihash_exsits_quiet(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)

        p = self.run_hashclient([
            "--address", self.server_address,
            "unihash-exists", unihash,
            "--quiet",
        ])
        self.assertEqual(p.returncode, 0)
        self.assertEqual(p.stdout.strip(), "")

        p = self.run_hashclient([
            "--address", self.server_address,
            "unihash-exists", '6662e699d6e3d894b24408ff9a4031ef9b038ee8',
            "--quiet",
        ])
        self.assertEqual(p.returncode, 1)
        self.assertEqual(p.stdout.strip(), "")

    def test_remove_taskhash(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        self.run_hashclient([
            "--address", self.server_address,
            "remove",
            "--where", "taskhash", taskhash,
        ], check=True)
        self.assertClientGetHash(self.client, taskhash, None)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_remove_unihash(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        self.run_hashclient([
            "--address", self.server_address,
            "remove",
            "--where", "unihash", unihash,
        ], check=True)
        self.assertClientGetHash(self.client, taskhash, None)

    def test_remove_outhash(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        self.run_hashclient([
            "--address", self.server_address,
            "remove",
            "--where", "outhash", outhash,
        ], check=True)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_remove_method(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)
        self.run_hashclient([
            "--address", self.server_address,
            "remove",
            "--where", "method", self.METHOD,
        ], check=True)
        self.assertClientGetHash(self.client, taskhash, None)

        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash)
        self.assertIsNone(result_outhash)

    def test_clean_unused(self):
        taskhash, outhash, unihash = self.create_test_hash(self.client)

        # Clean the database, which should not remove anything because all hashes an in-use
        self.run_hashclient([
            "--address", self.server_address,
            "clean-unused", "0",
        ], check=True)
        self.assertClientGetHash(self.client, taskhash, unihash)

        # Remove the unihash. The row in the outhash table should still be present
        self.run_hashclient([
            "--address", self.server_address,
            "remove",
            "--where", "unihash", unihash,
        ], check=True)
        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash, False)
        self.assertIsNotNone(result_outhash)

        # Now clean with no minimum age which will remove the outhash
        self.run_hashclient([
            "--address", self.server_address,
            "clean-unused", "0",
        ], check=True)
        result_outhash = self.client.get_outhash(self.METHOD, outhash, taskhash, False)
        self.assertIsNone(result_outhash)

    def test_refresh_token(self):
        admin_client = self.start_auth_server()

        user = admin_client.new_user("test-user", ["@read", "@report"])

        p = self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", user["username"],
            "--password", user["token"],
            "refresh-token"
        ], check=True)

        new_token = None
        for l in p.stdout.splitlines():
            l = l.rstrip()
            m = re.match(r'Token: +(.*)$', l)
            if m is not None:
                new_token = m.group(1)

        self.assertTrue(new_token)

        print("New token is %r" % new_token)

        self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", user["username"],
            "--password", new_token,
            "get-user"
        ], check=True)

    def test_set_user_perms(self):
        admin_client = self.start_auth_server()

        user = admin_client.new_user("test-user", ["@read"])

        self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", admin_client.username,
            "--password", admin_client.password,
            "set-user-perms",
            "-u", user["username"],
            "@read", "@report",
        ], check=True)

        new_user = admin_client.get_user(user["username"])

        self.assertEqual(set(new_user["permissions"]), {"@read", "@report"})

    def test_get_user(self):
        admin_client = self.start_auth_server()

        user = admin_client.new_user("test-user", ["@read"])

        p = self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", admin_client.username,
            "--password", admin_client.password,
            "get-user",
            "-u", user["username"],
        ], check=True)

        self.assertIn("Username:", p.stdout)
        self.assertIn("Permissions:", p.stdout)

        p = self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", user["username"],
            "--password", user["token"],
            "get-user",
        ], check=True)

        self.assertIn("Username:", p.stdout)
        self.assertIn("Permissions:", p.stdout)

    def test_get_all_users(self):
        admin_client = self.start_auth_server()

        admin_client.new_user("test-user1", ["@read"])
        admin_client.new_user("test-user2", ["@read"])

        p = self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", admin_client.username,
            "--password", admin_client.password,
            "get-all-users",
        ], check=True)

        self.assertIn("admin", p.stdout)
        self.assertIn("test-user1", p.stdout)
        self.assertIn("test-user2", p.stdout)

    def test_new_user(self):
        admin_client = self.start_auth_server()

        p = self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", admin_client.username,
            "--password", admin_client.password,
            "new-user",
            "-u", "test-user",
            "@read", "@report",
        ], check=True)

        new_token = None
        for l in p.stdout.splitlines():
            l = l.rstrip()
            m = re.match(r'Token: +(.*)$', l)
            if m is not None:
                new_token = m.group(1)

        self.assertTrue(new_token)

        user = {
            "username": "test-user",
            "token": new_token,
        }

        self.assertUserPerms(user, ["@read", "@report"])

    def test_delete_user(self):
        admin_client = self.start_auth_server()

        user = admin_client.new_user("test-user", ["@read"])

        p = self.run_hashclient([
            "--address", self.auth_server_address,
            "--login", admin_client.username,
            "--password", admin_client.password,
            "delete-user",
            "-u", user["username"],
        ], check=True)

        self.assertIsNone(admin_client.get_user(user["username"]))

    def test_get_db_usage(self):
        p = self.run_hashclient([
            "--address", self.server_address,
            "get-db-usage",
        ], check=True)

    def test_get_db_query_columns(self):
        p = self.run_hashclient([
            "--address", self.server_address,
            "get-db-query-columns",
        ], check=True)

    def test_gc(self):
        taskhash = '53b8dce672cb6d0c73170be43f540460bfc347b4'
        outhash = '5a9cb1649625f0bf41fc7791b635cd9c2d7118c7f021ba87dcd03f72b67ce7a8'
        unihash = 'f37918cc02eb5a520b1aff86faacbc0a38124646'

        result = self.client.report_unihash(taskhash, self.METHOD, outhash, unihash)
        self.assertEqual(result['unihash'], unihash, 'Server returned bad unihash')

        taskhash2 = '3bf6f1e89d26205aec90da04854fbdbf73afe6b4'
        outhash2 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash2 = 'af36b199320e611fbb16f1f277d3ee1d619ca58b'

        result = self.client.report_unihash(taskhash2, self.METHOD, outhash2, unihash2)
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        # Mark the first unihash to be kept
        self.run_hashclient([
            "--address", self.server_address,
            "gc-mark", "ABC",
            "--where", "unihash", unihash,
            "--where", "method", self.METHOD
        ], check=True)

        # Second hash is still there; mark doesn't delete hashes
        self.assertClientGetHash(self.client, taskhash2, unihash2)

        self.run_hashclient([
            "--address", self.server_address,
            "gc-sweep", "ABC",
        ], check=True)

        # Hash is gone. Taskhash is returned for second hash
        self.assertClientGetHash(self.client, taskhash2, None)
        # First hash is still present
        self.assertClientGetHash(self.client, taskhash, unihash)


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


class TestHashEquivalenceWebsocketServer(HashEquivalenceTestSetup, HashEquivalenceCommonTests, unittest.TestCase):
    def setUp(self):
        try:
            import websockets
        except ImportError as e:
            self.skipTest(str(e))

        super().setUp()

    def get_server_addr(self, server_idx):
        # Some hosts cause asyncio module to misbehave, when IPv6 is not enabled.
        # If IPv6 is enabled, it should be safe to use localhost directly, in general
        # case it is more reliable to resolve the IP address explicitly.
        host = socket.gethostbyname("localhost")
        return "ws://%s:0" % host


class TestHashEquivalenceWebsocketsSQLAlchemyServer(TestHashEquivalenceWebsocketServer):
    def setUp(self):
        try:
            import sqlalchemy
            import aiosqlite
        except ImportError as e:
            self.skipTest(str(e))

        super().setUp()

    def make_dbpath(self):
        return "sqlite+aiosqlite:///%s" % os.path.join(self.temp_dir.name, "db%d.sqlite" % self.server_index)


class TestHashEquivalenceExternalServer(HashEquivalenceTestSetup, HashEquivalenceCommonTests, unittest.TestCase):
    def get_env(self, name):
        v = os.environ.get(name)
        if not v:
            self.skipTest(f'{name} not defined to test an external server')
        return v

    def start_test_server(self):
        return self.get_env('BB_TEST_HASHSERV')

    def start_server(self, *args, **kwargs):
        self.skipTest('Cannot start local server when testing external servers')

    def start_auth_server(self):

        self.auth_server_address = self.server_address
        self.admin_client = self.start_client(
            self.server_address,
            username=self.get_env('BB_TEST_HASHSERV_USERNAME'),
            password=self.get_env('BB_TEST_HASHSERV_PASSWORD'),
        )
        return self.admin_client

    def setUp(self):
        super().setUp()
        if "BB_TEST_HASHSERV_USERNAME" in os.environ:
            self.client = self.start_client(
                self.server_address,
                username=os.environ["BB_TEST_HASHSERV_USERNAME"],
                password=os.environ["BB_TEST_HASHSERV_PASSWORD"],
            )
        self.client.remove({"method": self.METHOD})

    def tearDown(self):
        self.client.remove({"method": self.METHOD})
        super().tearDown()


    def test_auth_get_all_users(self):
        self.skipTest("Cannot test all users with external server")

