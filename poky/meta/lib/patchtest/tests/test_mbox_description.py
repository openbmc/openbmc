# Checks related to the patch's commit_message
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base

class CommitMessage(base.Base):

    def test_commit_message_presence(self):
        for commit in CommitMessage.commits:
            if not commit.commit_message.strip():
                self.fail('Patch is missing a descriptive commit message',
                          'Please include a commit message on your patch explaining the change (most importantly why the change is being made)',
                          commit)

