#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import json
import os
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, runCmd

class SPDXCheck(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(SPDXCheck, cls).setUpClass()
        bitbake("python3-spdx-tools-native")
        bitbake("-c addto_recipe_sysroot python3-spdx-tools-native")

    def check_recipe_spdx(self, high_level_dir, spdx_file, target_name):
        config = """
INHERIT += "create-spdx"
"""
        self.write_config(config)

        deploy_dir = get_bb_var("DEPLOY_DIR")
        machine_var = get_bb_var("MACHINE")
        spdx_version = get_bb_var("SPDX_VERSION")
        # qemux86-64 creates the directory qemux86_64
        machine_dir = machine_var.replace("-", "_")

        full_file_path = os.path.join(deploy_dir, "spdx", spdx_version, machine_dir, high_level_dir, spdx_file)

        try:
            os.remove(full_file_path)
        except FileNotFoundError:
            pass

        bitbake("%s -c create_spdx" % target_name)

        def check_spdx_json(filename):
            with open(filename) as f:
                report = json.load(f)
                self.assertNotEqual(report, None)
                self.assertNotEqual(report["SPDXID"], None)

            python = os.path.join(get_bb_var('STAGING_BINDIR', 'python3-spdx-tools-native'), 'nativepython3')
            validator = os.path.join(get_bb_var('STAGING_BINDIR', 'python3-spdx-tools-native'), 'pyspdxtools')
            result = runCmd("{} {} -i {}".format(python, validator, filename))

        self.assertExists(full_file_path)
        result = check_spdx_json(full_file_path)

    def test_spdx_base_files(self):
        self.check_recipe_spdx("packages", "base-files.spdx.json", "base-files")
