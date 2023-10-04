# Checks related to the patch's bugzilla tag
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import re
import base

class Bugzilla(base.Base):
    rexp_detect     = re.compile("\[\s?YOCTO.*\]", re.IGNORECASE)
    rexp_validation = re.compile("\[(\s?YOCTO\s?#\s?(\d+)\s?,?)+\]", re.IGNORECASE)

    def test_bugzilla_entry_format(self):
        for commit in Bugzilla.commits:
            for line in commit.commit_message.splitlines():
                if self.rexp_detect.match(line):
                    if not self.rexp_validation.match(line):
                        self.fail('Yocto Project bugzilla tag is not correctly formatted',
                                  'Specify bugzilla ID in commit description with format: "[YOCTO #<bugzilla ID>]"',
                                  commit)

