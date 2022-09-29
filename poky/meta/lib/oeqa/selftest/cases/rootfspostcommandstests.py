# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

import os
import oe
import unittest
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars

class ShadowUtilsTidyFiles(OESelftestTestCase):
    """
    Check if shadow image rootfs files are tidy.

    The tests are focused on testing the functionality provided by the
    'tidy_shadowutils_files' rootfs postprocess command (via
    SORT_PASSWD_POSTPROCESS_COMMAND).
    """

    def sysconf_build(self):
        """
        Verify if shadow tidy files tests are to be run and if yes, build a
        test image and return its sysconf rootfs path.
        """

        test_image = "core-image-minimal"

        config = 'IMAGE_CLASSES += "extrausers"\n'
        config += 'EXTRA_USERS_PARAMS = "groupadd -g 1000 oeqatester; "\n'
        config += 'EXTRA_USERS_PARAMS += "useradd -p \'\' -u 1000 -N -g 1000 oeqatester; "\n'
        self.write_config(config)

        vars = get_bb_vars(("IMAGE_ROOTFS", "SORT_PASSWD_POSTPROCESS_COMMAND", "sysconfdir"),
            test_image)
        passwd_postprocess_cmd = vars["SORT_PASSWD_POSTPROCESS_COMMAND"]
        self.assertIsNotNone(passwd_postprocess_cmd)
        if (passwd_postprocess_cmd.strip() != 'tidy_shadowutils_files;'):
            raise unittest.SkipTest("Testcase skipped as 'tidy_shadowutils_files' "
                "rootfs post process command is not the set SORT_PASSWD_POSTPROCESS_COMMAND.")

        rootfs = vars["IMAGE_ROOTFS"]
        self.assertIsNotNone(rootfs)
        sysconfdir = vars["sysconfdir"]
        bitbake(test_image)
        self.assertIsNotNone(sysconfdir)

        return oe.path.join(rootfs, sysconfdir)

    def test_shadowutils_backup_files(self):
        """
        Test that the rootfs doesn't include any known shadow backup files.
        """

        backup_files = (
            'group-',
            'gshadow-',
            'passwd-',
            'shadow-',
            'subgid-',
            'subuid-',
        )

        rootfs_sysconfdir = self.sysconf_build()
        found = []
        for backup_file in backup_files:
            backup_filepath = oe.path.join(rootfs_sysconfdir, backup_file)
            if os.path.exists(backup_filepath):
                found.append(backup_file)
        if (found):
            raise Exception('The following shadow backup files were found in '
                'the rootfs: %s' % found)

    def test_shadowutils_sorted_files(self):
        """
        Test that the 'passwd' and the 'group' shadow utils files are ordered
        by ID.
        """

        files = (
            'passwd',
            'group',
        )

        rootfs_sysconfdir = self.sysconf_build()
        unsorted = []
        for file in files:
            filepath = oe.path.join(rootfs_sysconfdir, file)
            with open(filepath, 'rb') as f:
                ids = []
                lines = f.readlines()
                for line in lines:
                    entries = line.split(b':')
                    ids.append(int(entries[2]))
            if (ids != sorted(ids)):
                unsorted.append(file)
        if (unsorted):
            raise Exception("The following files were not sorted by ID as expected: %s" % unsorted)
