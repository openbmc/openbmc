import datetime
import unittest
import os
import re
import shutil

import oeqa.utils.ftools as ftools
from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_test_layer


class SStateBase(oeSelfTest):

    def setUpLocal(self):
        self.temp_sstate_location = None
        self.sstate_path = get_bb_var('SSTATE_DIR')
        self.distro = get_bb_var('NATIVELSBSTRING')
        self.distro_specific_sstate = os.path.join(self.sstate_path, self.distro)

    # Creates a special sstate configuration with the option to add sstate mirrors
    def config_sstate(self, temp_sstate_location=False, add_local_mirrors=[]):
        self.temp_sstate_location = temp_sstate_location

        if self.temp_sstate_location:
            temp_sstate_path = os.path.join(self.builddir, "temp_sstate_%s" % datetime.datetime.now().strftime('%Y%m%d%H%M%S'))
            config_temp_sstate = "SSTATE_DIR = \"%s\"" % temp_sstate_path
            self.append_config(config_temp_sstate)
            self.track_for_cleanup(temp_sstate_path)
        self.sstate_path = get_bb_var('SSTATE_DIR')
        self.distro = get_bb_var('NATIVELSBSTRING')
        self.distro_specific_sstate = os.path.join(self.sstate_path, self.distro)

        if add_local_mirrors:
            config_set_sstate_if_not_set = 'SSTATE_MIRRORS ?= ""'
            self.append_config(config_set_sstate_if_not_set)
            for local_mirror in add_local_mirrors:
                self.assertFalse(os.path.join(local_mirror) == os.path.join(self.sstate_path), msg='Cannot add the current sstate path as a sstate mirror')
                config_sstate_mirror = "SSTATE_MIRRORS += \"file://.* file:///%s/PATH\"" % local_mirror
                self.append_config(config_sstate_mirror)

    # Returns a list containing sstate files
    def search_sstate(self, filename_regex, distro_specific=True, distro_nonspecific=True):
        result = []
        for root, dirs, files in os.walk(self.sstate_path):
            if distro_specific and re.search("%s/[a-z0-9]{2}$" % self.distro, root):
                for f in files:
                    if re.search(filename_regex, f):
                        result.append(f)
            if distro_nonspecific and re.search("%s/[a-z0-9]{2}$" % self.sstate_path, root):
                for f in files:
                    if re.search(filename_regex, f):
                        result.append(f)
        return result
