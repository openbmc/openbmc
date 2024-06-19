#! /usr/bin/env python3
#
# Copyright (C) 2024 BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from . import create_server, create_client, increase_revision, revision_greater, revision_smaller, _revision_greater_or_equal
import prserv.db as db
from bb.asyncrpc import InvokeError
import logging
import os
import sys
import tempfile
import unittest
import socket
import subprocess
from pathlib import Path

THIS_DIR = Path(__file__).parent
BIN_DIR = THIS_DIR.parent.parent / "bin"

version = "dummy-1.0-r0"
pkgarch = "core2-64"
other_arch = "aarch64"

checksumX = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4f0"
checksum0 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a0"
checksum1 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a1"
checksum2 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a2"
checksum3 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a3"
checksum4 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a4"
checksum5 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a5"
checksum6 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a6"
checksum7 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a7"
checksum8 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a8"
checksum9 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4a9"
checksum10 = "51bf8189dbe9ea81fa6dd89608bf19380c437a9cf12f6c6239887801ba4ab4aa"

def server_prefunc(server, name):
    logging.basicConfig(level=logging.DEBUG, filename='prserv-%s.log' % name, filemode='w',
                        format='%(levelname)s %(filename)s:%(lineno)d %(message)s')
    server.logger.debug("Running server %s" % name)
    sys.stdout = open('prserv-stdout-%s.log' % name, 'w')
    sys.stderr = sys.stdout

class PRTestSetup(object):

    def start_server(self, name, dbfile, upstream=None, read_only=False, prefunc=server_prefunc):

        def cleanup_server(server):
            if server.process.exitcode is not None:
                return
            server.process.terminate()
            server.process.join()

        server = create_server(socket.gethostbyname("localhost") + ":0",
                               dbfile,
                               upstream=upstream,
                               read_only=read_only)

        server.serve_as_process(prefunc=prefunc, args=(name,))
        self.addCleanup(cleanup_server, server)

        return server

    def start_client(self, server_address):
        def cleanup_client(client):
            client.close()

        client = create_client(server_address)
        self.addCleanup(cleanup_client, client)

        return client

class FunctionTests(unittest.TestCase):

    def setUp(self):
        self.temp_dir = tempfile.TemporaryDirectory(prefix='bb-prserv')
        self.addCleanup(self.temp_dir.cleanup)

    def test_increase_revision(self):
        self.assertEqual(increase_revision("1"), "2")
        self.assertEqual(increase_revision("1.0"), "1.1")
        self.assertEqual(increase_revision("1.1.1"), "1.1.2")
        self.assertEqual(increase_revision("1.1.1.3"), "1.1.1.4")
        self.assertRaises(ValueError, increase_revision, "1.a")
        self.assertRaises(ValueError, increase_revision, "1.")
        self.assertRaises(ValueError, increase_revision, "")

    def test_revision_greater_or_equal(self):
        self.assertTrue(_revision_greater_or_equal("2", "2"))
        self.assertTrue(_revision_greater_or_equal("2", "1"))
        self.assertTrue(_revision_greater_or_equal("10", "2"))
        self.assertTrue(_revision_greater_or_equal("1.10", "1.2"))
        self.assertFalse(_revision_greater_or_equal("1.2", "1.10"))
        self.assertTrue(_revision_greater_or_equal("1.10", "1"))
        self.assertTrue(_revision_greater_or_equal("1.10.1", "1.10"))
        self.assertFalse(_revision_greater_or_equal("1.10.1", "1.10.2"))
        self.assertTrue(_revision_greater_or_equal("1.10.1", "1.10.1"))
        self.assertTrue(_revision_greater_or_equal("1.10.1", "1"))
        self.assertTrue(revision_greater("1.20", "1.3"))
        self.assertTrue(revision_smaller("1.3", "1.20"))

    # DB tests

    def test_db(self):
        dbfile = os.path.join(self.temp_dir.name, "testtable.sqlite3")

        self.db = db.PRData(dbfile)
        self.table = self.db["PRMAIN"]

        self.table.store_value(version, pkgarch, checksum0, "0")
        self.table.store_value(version, pkgarch, checksum1, "1")
        # "No history" mode supports multiple PRs for the same checksum
        self.table.store_value(version, pkgarch, checksum0, "2")
        self.table.store_value(version, pkgarch, checksum2, "1.0")

        self.assertTrue(self.table.test_package(version, pkgarch))
        self.assertFalse(self.table.test_package(version, other_arch))

        self.assertTrue(self.table.test_value(version, pkgarch, "0"))
        self.assertTrue(self.table.test_value(version, pkgarch, "1"))
        self.assertTrue(self.table.test_value(version, pkgarch, "2"))

        self.assertEqual(self.table.find_package_max_value(version, pkgarch), "2")

        self.assertEqual(self.table.find_min_value(version, pkgarch, checksum0), "0")
        self.assertEqual(self.table.find_max_value(version, pkgarch, checksum0), "2")

        # Test history modes
        self.assertEqual(self.table.find_value(version, pkgarch, checksum0, True), "0")
        self.assertEqual(self.table.find_value(version, pkgarch, checksum0, False), "2")

        self.assertEqual(self.table.find_new_subvalue(version, pkgarch, "3"), "3.0")
        self.assertEqual(self.table.find_new_subvalue(version, pkgarch, "1"), "1.1")

        # Revision comparison tests
        self.table.store_value(version, pkgarch, checksum1, "1.3")
        self.table.store_value(version, pkgarch, checksum1, "1.20")
        self.assertEqual(self.table.find_min_value(version, pkgarch, checksum1), "1")
        self.assertEqual(self.table.find_max_value(version, pkgarch, checksum1), "1.20")

class PRBasicTests(PRTestSetup, unittest.TestCase):

    def setUp(self):
        self.temp_dir = tempfile.TemporaryDirectory(prefix='bb-prserv')
        self.addCleanup(self.temp_dir.cleanup)

        dbfile = os.path.join(self.temp_dir.name, "prtest-basic.sqlite3")

        self.server1 = self.start_server("basic", dbfile)
        self.client1 = self.start_client(self.server1.address)

    def test_basic(self):

        # Checks on non existing configuration

        result = self.client1.test_pr(version, pkgarch, checksum0)
        self.assertIsNone(result, "test_pr should return 'None' for a non existing PR")

        result = self.client1.test_package(version, pkgarch)
        self.assertFalse(result, "test_package should return 'False' for a non existing PR")

        result = self.client1.max_package_pr(version, pkgarch)
        self.assertIsNone(result, "max_package_pr should return 'None' for a non existing PR")

        # Add a first configuration

        result = self.client1.getPR(version, pkgarch, checksum0)
        self.assertEqual(result, "0", "getPR: initial PR of a package should be '0'")

        result = self.client1.test_pr(version, pkgarch, checksum0)
        self.assertEqual(result, "0", "test_pr should return '0' here, matching the result of getPR")

        result = self.client1.test_package(version, pkgarch)
        self.assertTrue(result, "test_package should return 'True' for an existing PR")

        result = self.client1.max_package_pr(version, pkgarch)
        self.assertEqual(result, "0", "max_package_pr should return '0' in the current test series")

        # Check that the same request gets the same value

        result = self.client1.getPR(version, pkgarch, checksum0)
        self.assertEqual(result, "0", "getPR: asking for the same PR a second time in a row should return the same value.")

        # Add new configurations

        result = self.client1.getPR(version, pkgarch, checksum1)
        self.assertEqual(result, "1", "getPR: second PR of a package should be '1'")

        result = self.client1.test_pr(version, pkgarch, checksum1)
        self.assertEqual(result, "1", "test_pr should return '1' here, matching the result of getPR")

        result = self.client1.max_package_pr(version, pkgarch)
        self.assertEqual(result, "1", "max_package_pr should return '1' in the current test series")

        result = self.client1.getPR(version, pkgarch, checksum2)
        self.assertEqual(result, "2", "getPR: second PR of a package should be '2'")

        result = self.client1.test_pr(version, pkgarch, checksum2)
        self.assertEqual(result, "2", "test_pr should return '2' here, matching the result of getPR")

        result = self.client1.max_package_pr(version, pkgarch)
        self.assertEqual(result, "2", "max_package_pr should return '2' in the current test series")

        result = self.client1.getPR(version, pkgarch, checksum3)
        self.assertEqual(result, "3", "getPR: second PR of a package should be '3'")

        result = self.client1.test_pr(version, pkgarch, checksum3)
        self.assertEqual(result, "3", "test_pr should return '3' here, matching the result of getPR")

        result = self.client1.max_package_pr(version, pkgarch)
        self.assertEqual(result, "3", "max_package_pr should return '3' in the current test series")

        # Ask again for the first configuration

        result = self.client1.getPR(version, pkgarch, checksum0)
        self.assertEqual(result, "4", "getPR: should return '4' in this configuration")

        # Ask again with explicit "no history" mode

        result = self.client1.getPR(version, pkgarch, checksum0, False)
        self.assertEqual(result, "4", "getPR: should return '4' in this configuration")

        # Ask again with explicit "history" mode. This should return the first recorded PR for checksum0

        result = self.client1.getPR(version, pkgarch, checksum0, True)
        self.assertEqual(result, "0", "getPR: should return '0' in this configuration")

        # Check again that another pkgarg resets the counters

        result = self.client1.test_pr(version, other_arch, checksum0)
        self.assertIsNone(result, "test_pr should return 'None' for a non existing PR")

        result = self.client1.test_package(version, other_arch)
        self.assertFalse(result, "test_package should return 'False' for a non existing PR")

        result = self.client1.max_package_pr(version, other_arch)
        self.assertIsNone(result, "max_package_pr should return 'None' for a non existing PR")

        # Now add the configuration

        result = self.client1.getPR(version, other_arch, checksum0)
        self.assertEqual(result, "0", "getPR: initial PR of a package should be '0'")

        result = self.client1.test_pr(version, other_arch, checksum0)
        self.assertEqual(result, "0", "test_pr should return '0' here, matching the result of getPR")

        result = self.client1.test_package(version, other_arch)
        self.assertTrue(result, "test_package should return 'True' for an existing PR")

        result = self.client1.max_package_pr(version, other_arch)
        self.assertEqual(result, "0", "max_package_pr should return '0' in the current test series")

        result = self.client1.is_readonly()
        self.assertFalse(result, "Server should not be described as 'read-only'")

class PRUpstreamTests(PRTestSetup, unittest.TestCase):

    def setUp(self):

        self.temp_dir = tempfile.TemporaryDirectory(prefix='bb-prserv')
        self.addCleanup(self.temp_dir.cleanup)

        dbfile2 = os.path.join(self.temp_dir.name, "prtest-upstream2.sqlite3")
        self.server2 = self.start_server("upstream2", dbfile2)
        self.client2 = self.start_client(self.server2.address)

        dbfile1 = os.path.join(self.temp_dir.name, "prtest-upstream1.sqlite3")
        self.server1 = self.start_server("upstream1", dbfile1, upstream=self.server2.address)
        self.client1 = self.start_client(self.server1.address)

        dbfile0 = os.path.join(self.temp_dir.name, "prtest-local.sqlite3")
        self.server0 = self.start_server("local", dbfile0, upstream=self.server1.address)
        self.client0 = self.start_client(self.server0.address)
        self.shared_db = dbfile0

    def test_upstream_and_readonly(self):

        # For identical checksums, all servers should return the same PR

        result = self.client2.getPR(version, pkgarch, checksum0)
        self.assertEqual(result, "0", "getPR: initial PR of a package should be '0'")

        result = self.client1.getPR(version, pkgarch, checksum0)
        self.assertEqual(result, "0", "getPR: initial PR of a package should be '0' (same as upstream)")

        result = self.client0.getPR(version, pkgarch, checksum0)
        self.assertEqual(result, "0", "getPR: initial PR of a package should be '0' (same as upstream)")

        # Now introduce new checksums on server1 for, same version

        result = self.client1.getPR(version, pkgarch, checksum1)
        self.assertEqual(result, "0.0", "getPR: first PR of a package which has a different checksum upstream should be '0.0'")

        result = self.client1.getPR(version, pkgarch, checksum2)
        self.assertEqual(result, "0.1", "getPR: second PR of a package that has a different checksum upstream should be '0.1'")

        # Now introduce checksums on server0 for, same version

        result = self.client1.getPR(version, pkgarch, checksum1)
        self.assertEqual(result, "0.2", "getPR: can't decrease for known PR")

        result = self.client1.getPR(version, pkgarch, checksum2)
        self.assertEqual(result, "0.3")

        result = self.client1.max_package_pr(version, pkgarch)
        self.assertEqual(result, "0.3")

        result = self.client0.getPR(version, pkgarch, checksum3)
        self.assertEqual(result, "0.3.0", "getPR: first PR of a package that doesn't exist upstream should be '0.3.0'")

        result = self.client0.getPR(version, pkgarch, checksum4)
        self.assertEqual(result, "0.3.1", "getPR: second PR of a package that doesn't exist upstream should be '0.3.1'")

        result = self.client0.getPR(version, pkgarch, checksum3)
        self.assertEqual(result, "0.3.2")

        # More upstream updates
        # Here, we assume no communication between server2 and server0. server2 only impacts server0
        # after impacting server1

        self.assertEqual(self.client2.getPR(version, pkgarch, checksum5), "1")
        self.assertEqual(self.client1.getPR(version, pkgarch, checksum6), "1.0")
        self.assertEqual(self.client1.getPR(version, pkgarch, checksum7), "1.1")
        self.assertEqual(self.client0.getPR(version, pkgarch, checksum8), "1.1.0")
        self.assertEqual(self.client0.getPR(version, pkgarch, checksum9), "1.1.1")

        # "history" mode tests

        self.assertEqual(self.client2.getPR(version, pkgarch, checksum0, True), "0")
        self.assertEqual(self.client1.getPR(version, pkgarch, checksum2, True), "0.1")
        self.assertEqual(self.client0.getPR(version, pkgarch, checksum3, True), "0.3.0")

        # More "no history" mode tests

        self.assertEqual(self.client2.getPR(version, pkgarch, checksum0), "2")
        self.assertEqual(self.client1.getPR(version, pkgarch, checksum0), "2") # Same as upstream
        self.assertEqual(self.client0.getPR(version, pkgarch, checksum0), "2") # Same as upstream
        self.assertEqual(self.client1.getPR(version, pkgarch, checksum7), "3") # This could be surprising, but since the previous revision was "2", increasing it yields "3".
                                                                               # We don't know how many upstream servers we have
        # Start read-only server with server1 as upstream
        self.server_ro = self.start_server("local-ro", self.shared_db, upstream=self.server1.address, read_only=True)
        self.client_ro = self.start_client(self.server_ro.address)

        self.assertTrue(self.client_ro.is_readonly(), "Database should be described as 'read-only'")

        # Checks on non existing configurations
        self.assertIsNone(self.client_ro.test_pr(version, pkgarch, checksumX))
        self.assertFalse(self.client_ro.test_package("unknown", pkgarch))

        # Look up existing configurations
        self.assertEqual(self.client_ro.getPR(version, pkgarch, checksum0), "3") # "no history" mode
        self.assertEqual(self.client_ro.getPR(version, pkgarch, checksum0, True), "0") # "history" mode
        self.assertEqual(self.client_ro.getPR(version, pkgarch, checksum3), "3")
        self.assertEqual(self.client_ro.getPR(version, pkgarch, checksum3, True), "0.3.0")
        self.assertEqual(self.client_ro.max_package_pr(version, pkgarch), "2") # normal as "3" was never saved

        # Try to insert a new value. Here this one is know upstream.
        self.assertEqual(self.client_ro.getPR(version, pkgarch, checksum7), "3")
        # Try to insert a completely new value. As the max upstream value is already "3", it should be "3.0"
        self.assertEqual(self.client_ro.getPR(version, pkgarch, checksum10), "3.0")
        # Same with another value which only exists in the upstream upstream server
        # This time, as the upstream server doesn't know it, it will ask its upstream server. So that's a known one.
        self.assertEqual(self.client_ro.getPR(version, pkgarch, checksum9), "3")

class ScriptTests(unittest.TestCase):

    def setUp(self):

        self.temp_dir = tempfile.TemporaryDirectory(prefix='bb-prserv')
        self.addCleanup(self.temp_dir.cleanup)
        self.dbfile = os.path.join(self.temp_dir.name, "prtest.sqlite3")

    def test_1_start_bitbake_prserv(self):
        try:
            subprocess.check_call([BIN_DIR / "bitbake-prserv", "--start", "-f", self.dbfile])
        except subprocess.CalledProcessError as e:
            self.fail("Failed to start bitbake-prserv: %s" % e.returncode)

    def test_2_stop_bitbake_prserv(self):
        try:
            subprocess.check_call([BIN_DIR / "bitbake-prserv", "--stop"])
        except subprocess.CalledProcessError as e:
            self.fail("Failed to stop bitbake-prserv: %s" % e.returncode)
