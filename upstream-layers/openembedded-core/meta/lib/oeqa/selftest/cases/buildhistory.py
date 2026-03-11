#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import re
import datetime

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars, get_bb_var, runCmd


class BuildhistoryTests(OESelftestTestCase):

    def config_buildhistory(self, tmp_bh_location=False):
        bb_vars = get_bb_vars(['USER_CLASSES', 'INHERIT'])
        if (not 'buildhistory' in bb_vars['USER_CLASSES']) and (not 'buildhistory' in bb_vars['INHERIT']):
            add_buildhistory_config = 'INHERIT += "buildhistory"\nBUILDHISTORY_COMMIT = "1"'
            self.append_config(add_buildhistory_config)

        if tmp_bh_location:
            # Using a temporary buildhistory location for testing
            tmp_bh_dir = os.path.join(self.builddir, "tmp_buildhistory_%s" % datetime.datetime.now().strftime('%Y%m%d%H%M%S'))
            buildhistory_dir_config = "BUILDHISTORY_DIR = \"%s\"" % tmp_bh_dir
            self.append_config(buildhistory_dir_config)
            self.track_for_cleanup(tmp_bh_dir)

    def run_buildhistory_operation(self, target, global_config='', target_config='', change_bh_location=False, expect_error=False, error_regex=''):
        if change_bh_location:
            tmp_bh_location = True
        else:
            tmp_bh_location = False
        self.config_buildhistory(tmp_bh_location)

        self.append_config(global_config)
        self.append_recipeinc(target, target_config)
        bitbake("-cclean %s" % target)
        result = bitbake(target, ignore_status=True)
        self.remove_config(global_config)
        self.remove_recipeinc(target, target_config)

        if expect_error:
            self.assertEqual(result.status, 1, msg="Error expected for global config '%s' and target config '%s'" % (global_config, target_config))
            search_for_error = re.search(error_regex, result.output)
            self.assertTrue(search_for_error, msg="Could not find desired error in output: %s (%s)" % (error_regex, result.output))
        else:
            self.assertEqual(result.status, 0, msg="Command 'bitbake %s' has failed unexpectedly: %s" % (target, result.output))


    def test_buildhistory_basic(self):
        self.run_buildhistory_operation('xcursor-transparent-theme')
        self.assertTrue(os.path.isdir(get_bb_var('BUILDHISTORY_DIR')), "buildhistory dir was not created.")

    def test_buildhistory_buildtime_pr_backwards(self):
        target = 'xcursor-transparent-theme'
        error = "ERROR:.*QA Issue: Package version for package %s went backwards which would break package feeds \(from .*-r1.* to .*-r0.*\)" % target
        self.run_buildhistory_operation(target, target_config="PR = \"r1\"", change_bh_location=True)
        self.run_buildhistory_operation(target, target_config="PR = \"r0\"", change_bh_location=False, expect_error=True, error_regex=error)

    def test_fileinfo(self):
        self.config_buildhistory()
        bitbake('hicolor-icon-theme')
        history_dir = get_bb_var('BUILDHISTORY_DIR_PACKAGE', 'hicolor-icon-theme')
        self.assertTrue(os.path.isdir(history_dir), 'buildhistory dir was not created.')

        def load_bh(f):
            d = {}
            for line in open(f):
                split = [s.strip() for s in line.split('=', 1)]
                if len(split) > 1:
                    d[split[0]] = split[1]
            return d

        data = load_bh(os.path.join(history_dir, 'hicolor-icon-theme', 'latest'))
        self.assertIn('FILELIST', data)
        self.assertEqual(data['FILELIST'], '/usr/share/icons/hicolor/index.theme')
        self.assertGreater(int(data['PKGSIZE']), 0)

        data = load_bh(os.path.join(history_dir, 'hicolor-icon-theme-dev', 'latest'))
        if 'FILELIST' in data:
            self.assertEqual(data['FILELIST'], '/usr/share/pkgconfig/default-icon-theme.pc')
        self.assertGreater(int(data['PKGSIZE']), 0)

    def test_buildhistory_diff(self):
        target = 'xcursor-transparent-theme'
        self.run_buildhistory_operation(target, target_config="PR = \"r1\"", change_bh_location=True)
        self.run_buildhistory_operation(target, target_config="PR = \"r0\"", change_bh_location=False, expect_error=True)
        result = runCmd("oe-pkgdata-util read-value PKGV %s" % target)
        pkgv = result.output.rstrip()
        result = runCmd("buildhistory-diff -p %s" % get_bb_var('BUILDHISTORY_DIR'))
        expected_endlines = [
            "xcursor-transparent-theme-dev: RRECOMMENDS: removed \"xcursor-transparent-theme (['= %s-r1'])\", added \"xcursor-transparent-theme (['= %s-r0'])\"" % (pkgv, pkgv),
            "xcursor-transparent-theme-staticdev: RDEPENDS: removed \"xcursor-transparent-theme-dev (['= %s-r1'])\", added \"xcursor-transparent-theme-dev (['= %s-r0'])\"" % (pkgv, pkgv)
        ]
        for line in result.output.splitlines():
            for el in expected_endlines:
                if line.endswith(el):
                    expected_endlines.remove(el)
                    break
            else:
                self.fail('Unexpected line:\n%s\nExpected line endings:\n  %s' % (line, '\n  '.join(expected_endlines)))
        if expected_endlines:
            self.fail('Missing expected line endings:\n  %s' % '\n  '.join(expected_endlines))