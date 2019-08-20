#! /usr/bin/env python3
#
# Copyright (C) 2018 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import multiprocessing
import sqlite3
import hashlib
import urllib.request
import json
import tempfile
from . import create_server

class TestHashEquivalenceServer(unittest.TestCase):
    def setUp(self):
        # Start a hash equivalence server in the background bound to
        # an ephemeral port
        self.dbfile = tempfile.NamedTemporaryFile(prefix="bb-hashserv-db-")
        self.server = create_server(('localhost', 0), self.dbfile.name)
        self.server_addr = 'http://localhost:%d' % self.server.socket.getsockname()[1]
        self.server_thread = multiprocessing.Process(target=self.server.serve_forever)
        self.server_thread.daemon = True
        self.server_thread.start()

    def tearDown(self):
        # Shutdown server
        s = getattr(self, 'server', None)
        if s is not None:
            self.server_thread.terminate()
            self.server_thread.join()

    def send_get(self, path):
        url = '%s/%s' % (self.server_addr, path)
        request = urllib.request.Request(url)
        response = urllib.request.urlopen(request)
        return json.loads(response.read().decode('utf-8'))

    def send_post(self, path, data):
        headers = {'content-type': 'application/json'}
        url = '%s/%s' % (self.server_addr, path)
        request = urllib.request.Request(url, json.dumps(data).encode('utf-8'), headers)
        response = urllib.request.urlopen(request)
        return json.loads(response.read().decode('utf-8'))

    def test_create_hash(self):
        # Simple test that hashes can be created
        taskhash = '35788efcb8dfb0a02659d81cf2bfd695fb30faf9'
        outhash = '2765d4a5884be49b28601445c2760c5f21e7e5c0ee2b7e3fce98fd7e5970796f'
        unihash = 'f46d3fbb439bd9b921095da657a4de906510d2cd'

        d = self.send_get('v1/equivalent?method=TestMethod&taskhash=%s' % taskhash)
        self.assertIsNone(d, msg='Found unexpected task, %r' % d)

        d = self.send_post('v1/equivalent', {
            'taskhash': taskhash,
            'method': 'TestMethod',
            'outhash': outhash,
            'unihash': unihash,
            })
        self.assertEqual(d['unihash'], unihash, 'Server returned bad unihash')

    def test_create_equivalent(self):
        # Tests that a second reported task with the same outhash will be
        # assigned the same unihash
        taskhash = '53b8dce672cb6d0c73170be43f540460bfc347b4'
        outhash = '5a9cb1649625f0bf41fc7791b635cd9c2d7118c7f021ba87dcd03f72b67ce7a8'
        unihash = 'f37918cc02eb5a520b1aff86faacbc0a38124646'
        d = self.send_post('v1/equivalent', {
            'taskhash': taskhash,
            'method': 'TestMethod',
            'outhash': outhash,
            'unihash': unihash,
            })
        self.assertEqual(d['unihash'], unihash, 'Server returned bad unihash')

        # Report a different task with the same outhash. The returned unihash
        # should match the first task
        taskhash2 = '3bf6f1e89d26205aec90da04854fbdbf73afe6b4'
        unihash2 = 'af36b199320e611fbb16f1f277d3ee1d619ca58b'
        d = self.send_post('v1/equivalent', {
            'taskhash': taskhash2,
            'method': 'TestMethod',
            'outhash': outhash,
            'unihash': unihash2,
            })
        self.assertEqual(d['unihash'], unihash, 'Server returned bad unihash')

    def test_duplicate_taskhash(self):
        # Tests that duplicate reports of the same taskhash with different
        # outhash & unihash always return the unihash from the first reported
        # taskhash
        taskhash = '8aa96fcffb5831b3c2c0cb75f0431e3f8b20554a'
        outhash = 'afe240a439959ce86f5e322f8c208e1fedefea9e813f2140c81af866cc9edf7e'
        unihash = '218e57509998197d570e2c98512d0105985dffc9'
        d = self.send_post('v1/equivalent', {
            'taskhash': taskhash,
            'method': 'TestMethod',
            'outhash': outhash,
            'unihash': unihash,
            })

        d = self.send_get('v1/equivalent?method=TestMethod&taskhash=%s' % taskhash)
        self.assertEqual(d['unihash'], unihash)

        outhash2 = '0904a7fe3dc712d9fd8a74a616ddca2a825a8ee97adf0bd3fc86082c7639914d'
        unihash2 = 'ae9a7d252735f0dafcdb10e2e02561ca3a47314c'
        d = self.send_post('v1/equivalent', {
            'taskhash': taskhash,
            'method': 'TestMethod',
            'outhash': outhash2,
            'unihash': unihash2
            })

        d = self.send_get('v1/equivalent?method=TestMethod&taskhash=%s' % taskhash)
        self.assertEqual(d['unihash'], unihash)

        outhash3 = '77623a549b5b1a31e3732dfa8fe61d7ce5d44b3370f253c5360e136b852967b4'
        unihash3 = '9217a7d6398518e5dc002ed58f2cbbbc78696603'
        d = self.send_post('v1/equivalent', {
            'taskhash': taskhash,
            'method': 'TestMethod',
            'outhash': outhash3,
            'unihash': unihash3
            })

        d = self.send_get('v1/equivalent?method=TestMethod&taskhash=%s' % taskhash)
        self.assertEqual(d['unihash'], unihash)


