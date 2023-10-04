# Checks related to the patch's author
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
import re

class Author(base.Base):

    auh_email = '<auh@auh.yoctoproject.org>'

    invalids = [re.compile("^Upgrade Helper.+"),
                re.compile(re.escape(auh_email)),
                re.compile("uh@not\.set"),
                re.compile("\S+@example\.com")]


    def test_author_valid(self):
        for commit in self.commits:
            for invalid in self.invalids:
                if invalid.search(commit.author):
                    self.fail('Invalid author %s' % commit.author, 'Resend the series with a valid patch\'s author', commit)

    def test_non_auh_upgrade(self):
        for commit in self.commits:
            if self.auh_email in commit.payload:
                self.fail('Invalid author %s in commit message' % self.auh_email, 'Resend the series with a valid patch\'s author', commit)
