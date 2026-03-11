#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import sys
import subprocess
import shutil
from oeqa.selftest.case import OESelftestTestCase
from yocto_testresults_query import get_sha1, create_workdir
basepath = os.path.abspath(os.path.dirname(__file__) + '/../../../../../')
lib_path = basepath + '/scripts/lib'
sys.path = sys.path + [lib_path]


class TestResultsQueryTests(OESelftestTestCase):
    def test_get_sha1(self):
        test_data_get_sha1 = [
            {"input": "yocto-4.0", "expected": "00cfdde791a0176c134f31e5a09eff725e75b905"},
            {"input": "4.1_M1", "expected": "95066dde6861ee08fdb505ab3e0422156cc24fae"},
        ]
        for data in test_data_get_sha1:
            test_name = data["input"]
            with self.subTest(f"Test SHA1 from {test_name}"):
                self.assertEqual(
                    get_sha1(basepath, data["input"]), data["expected"])

    def test_create_workdir(self):
        workdir = create_workdir()
        try:
            url = subprocess.check_output(
                ["git", "-C", workdir, "remote", "get-url", "origin"]).strip().decode("utf-8")
        except:
            shutil.rmtree(workdir, ignore_errors=True)
            self.fail(f"Can not execute git commands in {workdir}")
        shutil.rmtree(workdir)
        self.assertEqual(url, "git://git.yoctoproject.org/yocto-testresults")
