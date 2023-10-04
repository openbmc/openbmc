# Checks related to the patch's signed-off-by lines
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
import parse_signed_off_by
import re

class SignedOffBy(base.Base):

    revert_shortlog_regex = re.compile('Revert\s+".*"')

    @classmethod
    def setUpClassLocal(cls):
        # match self.mark with no '+' preceding it
        cls.prog = parse_signed_off_by.signed_off_by

    def test_signed_off_by_presence(self):
        for commit in SignedOffBy.commits:
            # skip those patches that revert older commits, these do not required the tag presence
            if self.revert_shortlog_regex.match(commit.shortlog):
                continue
            if not SignedOffBy.prog.search_string(commit.payload):
                self.fail('Patch is missing Signed-off-by',
                          'Sign off the patch (either manually or with "git commit --amend -s")',
                          commit)
