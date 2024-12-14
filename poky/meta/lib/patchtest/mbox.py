#! /usr/bin/env python3

# series.py
#
# Read a series' mbox file and get information about the patches
# contained
#
# Copyright (C) 2024 BayLibre SAS
#
# SPDX-License-Identifier: GPL-2.0-only
#

import email
import re

# From: https://stackoverflow.com/questions/59681461/read-a-big-mbox-file-with-python
class MboxReader:
    def __init__(self, filepath):
        self.handle = open(filepath, 'rb')
        assert self.handle.readline().startswith(b'From ')

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_value, exc_traceback):
        self.handle.close()

    def __iter__(self):
        return iter(self.__next__())

    def __next__(self):
        lines = []
        while True:
            line = self.handle.readline()
            if line == b'' or line.startswith(b'From '):
                yield email.message_from_bytes(b''.join(lines))
                if line == b'':
                    break
                lines = []
                continue
            lines.append(line)

class Patch:
    def __init__(self, data):
        self.author = data['From']
        self.to = data['To']
        self.cc = data['Cc']
        self.subject = data['Subject']
        self.split_body = re.split('---', data.get_payload(), maxsplit=1)
        self.commit_message = self.split_body[0]
        self.diff = self.split_body[1]

class PatchSeries:
    def __init__(self, filepath):
        with MboxReader(filepath) as mbox:
            self.patches = [Patch(message) for message in mbox]

        assert self.patches
        self.patch_count = len(self.patches)
        self.path = filepath

        @property
        def path(self):
            return self.path

        self.branch = self.get_branch()

    def get_branch(self):
        fullprefix = ""
        pattern = re.compile(r"(\[.*\])", re.DOTALL)

        # There should be at least one patch in the series and it should
        # include the branch name in the subject, so parse that
        match = pattern.search(self.patches[0].subject)
        if match:
            fullprefix = match.group(1)

        branch, branches, valid_branches = None, [], []

        if fullprefix:
            prefix = fullprefix.strip('[]')
            branches = [ b.strip() for b in prefix.split(',')]
            valid_branches = [b for b in branches if PatchSeries.valid_branch(b)]

        if len(valid_branches):
            branch = valid_branches[0]

        # Get the branch name excluding any brackets. If nothing was
        # found, then assume there was no branch tag in the subject line
        # and that the patch targets master
        if branch is not None:
            return branch.split(']')[0]
        else:
            return "master"

    @staticmethod
    def valid_branch(branch):
        """ Check if branch is valid name """
        lbranch = branch.lower()

        invalid  = lbranch.startswith('patch') or \
                   lbranch.startswith('rfc') or \
                   lbranch.startswith('resend') or \
                   re.search(r'^v\d+', lbranch) or \
                   re.search(r'^\d+/\d+', lbranch)

        return not invalid

