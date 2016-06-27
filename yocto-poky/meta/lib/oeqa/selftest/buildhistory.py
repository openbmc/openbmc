import os
import re
import datetime

from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import bitbake, get_bb_var
from oeqa.utils.decorators import testcase


class BuildhistoryBase(oeSelfTest):

    def config_buildhistory(self, tmp_bh_location=False):
        if (not 'buildhistory' in get_bb_var('USER_CLASSES')) and (not 'buildhistory' in get_bb_var('INHERIT')):
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

    # No tests should be added to the base class.
    # Please create a new class that inherit this one, or use one of those already available for adding tests.
