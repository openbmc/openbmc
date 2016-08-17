import datetime
import unittest
import os
import re
import shutil

import oeqa.utils.ftools as ftools
from oeqa.selftest.base import oeSelfTest
from oeqa.selftest.buildhistory import BuildhistoryBase
from oeqa.utils.commands import Command, runCmd, bitbake, get_bb_var, get_test_layer
from oeqa.utils.decorators import testcase

class TestScripts(oeSelfTest):

    @testcase(300)
    def test_cleanup_workdir(self):
        path = os.path.dirname(get_bb_var('WORKDIR', 'gzip'))
        old_version_recipe = os.path.join(get_bb_var('COREBASE'), 'meta/recipes-extended/gzip/gzip_1.3.12.bb')
        old_version = '1.3.12'
        bitbake("-ccleansstate gzip")
        bitbake("-ccleansstate -b %s" % old_version_recipe)
        if os.path.exists(get_bb_var('WORKDIR', "-b %s" % old_version_recipe)):
            shutil.rmtree(get_bb_var('WORKDIR', "-b %s" % old_version_recipe))
        if os.path.exists(get_bb_var('WORKDIR', 'gzip')):
            shutil.rmtree(get_bb_var('WORKDIR', 'gzip'))

        if os.path.exists(path):
            initial_contents = os.listdir(path)
        else:
            initial_contents = []

        bitbake('gzip')
        intermediary_contents = os.listdir(path)
        bitbake("-b %s" % old_version_recipe)
        runCmd('cleanup-workdir')
        remaining_contents = os.listdir(path)

        expected_contents = [x for x in intermediary_contents if x not in initial_contents]
        remaining_not_expected = [x for x in remaining_contents if x not in expected_contents]
        self.assertFalse(remaining_not_expected, msg="Not all necessary content has been deleted from %s: %s" % (path, ', '.join(map(str, remaining_not_expected))))
        expected_not_remaining = [x for x in expected_contents if x not in remaining_contents]
        self.assertFalse(expected_not_remaining, msg="The script removed extra contents from %s: %s" % (path, ', '.join(map(str, expected_not_remaining))))

class BuildhistoryDiffTests(BuildhistoryBase):

    @testcase(295)
    def test_buildhistory_diff(self):
        self.add_command_to_tearDown('cleanup-workdir')
        target = 'xcursor-transparent-theme'
        self.run_buildhistory_operation(target, target_config="PR = \"r1\"", change_bh_location=True)
        self.run_buildhistory_operation(target, target_config="PR = \"r0\"", change_bh_location=False, expect_error=True)
        result = runCmd("buildhistory-diff -p %s" % get_bb_var('BUILDHISTORY_DIR'))
        expected_output = 'PR changed from "r1" to "r0"'
        self.assertTrue(expected_output in result.output, msg="Did not find expected output: %s" % result.output)
