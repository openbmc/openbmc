#
# BitBake Test for lib/bb/siggen.py
#
# Copyright (C) 2020 Jean-François Dagenais
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import logging
import bb
import bb.data
import time
from contextlib import contextmanager

logger = logging.getLogger('BitBake.TestSiggen')

import bb.siggen

class SiggenTest(unittest.TestCase):

    def test_build_pnid(self):
        tests = {
            ('', 'helloworld', 'do_sometask') : 'helloworld:do_sometask',
            ('XX', 'helloworld', 'do_sometask') : 'mc:XX:helloworld:do_sometask',
        }

        for t in tests:
            self.assertEqual(bb.siggen.build_pnid(*t), tests[t])

    def test_get_unihashes_rejects_invalid_hashserv_unihash(self):
        class TestClient:
            def get_unihash_batch(self, query):
                list(query)
                return ["${@os.system('true')}"]

        class TestSiggen(bb.siggen.SignatureGeneratorUniHashMixIn):
            def __init__(self):
                self.server = "test-server"
                self.method = "test-method"
                self.extramethod = {}
                self.taskhash = {"test.bb:do_compile": "a" * 64}
                self.unihash = {}
                self.unitaskhashes = {}
                self.tidtopn = {}
                self.setscenetasks = set()

            @contextmanager
            def client(self):
                yield TestClient()

        siggen = TestSiggen()

        with self.assertRaises(bb.BBHandledException):
            siggen.get_unihashes(["test.bb:do_compile"])

        self.assertEqual(siggen.unihash, {})
        self.assertEqual(siggen.unitaskhashes, {})

    def test_report_unihash_reads_bb_unihash_without_expansion(self):
        class TestSiggen(bb.siggen.SignatureGeneratorUniHashMixIn):
            def __init__(self):
                self.setscenetasks = set()
                self.taskhash = {"test.bb:do_compile": "b" * 64}

        d = bb.data.init()
        d.setVar("BB_TASKHASH", "a" * 64)
        d.setVar("BB_UNIHASH", "${@d.setVar('EXPANDED_UNIHASH', '1') or 'bad'}")
        d.setVar("SSTATE_HASHEQUIV_REPORT_TASKDATA", "0")
        d.setVar("T", "/tmp")
        d.setVar("BB_FILENAME", "test.bb")

        TestSiggen().report_unihash(".", "compile", d)

        self.assertIsNone(d.getVar("EXPANDED_UNIHASH"))
