# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# patchtestrepo: PatchTestRepo class used mainly to control a git repo from patchtest
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import git
import os
import mbox

class PatchTestRepo(object):

    # prefixes used for temporal branches/stashes
    prefix = 'patchtest'

    def __init__(self, patch, repodir, commit=None, branch=None):
        self.repodir = repodir
        self.repo = git.Repo.init(repodir)
        self.patch = mbox.PatchSeries(patch)

        if self.repo.head.is_detached:
            self.current_commit = self.repo.head.commit.hexsha
            self.current_branch = None
        else:
            self.current_branch = self.repo.active_branch.name

        # targeted branch defined on the patch may be invalid, so make sure there
        # is a corresponding remote branch
        valid_patch_branch = None
        if self.patch.branch in self.repo.branches:
            valid_patch_branch = self.patch.branch
            
        # Target Commit
        # Priority (top has highest priority):
        #    1. commit given at cmd line
        #    2. branch given at cmd line
        #    3. branch given at the patch
        #    3. current HEAD
        self._commit = self._get_commitid(commit) or \
          self._get_commitid(branch) or \
          self._get_commitid(valid_patch_branch) or \
          self._get_commitid('HEAD')

        self._workingbranch = "%s_%s" % (PatchTestRepo.prefix, os.getpid())

        # create working branch. Use the '-B' flag so that we just
        # check out the existing one if it's there
        self.repo.git.execute(['git', 'checkout', '-B', self._workingbranch, self._commit])

        self._patchmerged = False

        # Check if patch can be merged using git-am
        self._patchcanbemerged = True
        try:
            # Make sure to get the absolute path of the file
            self.repo.git.execute(['git', '-C', self.repodir, 'apply', '--check', os.path.abspath(self.patch.path)], with_exceptions=True)
        except git.exc.GitCommandError as ce:
            self._patchcanbemerged = False

    def ismerged(self):
        return self._patchmerged

    def canbemerged(self):
        return self._patchcanbemerged

    def _get_commitid(self, commit):

        if not commit:
            return None

        try:
            return self.repo.rev_parse(commit).hexsha
        except Exception as e:
            print(f"Couldn't find commit {commit} in repo")

        return None

    def merge(self):
        if self._patchcanbemerged:
            self.repo.git.execute(['git', '-C', self.repodir, 'am', '--keep-cr', os.path.abspath(self.patch.path)])
            self._patchmerged = True

    def clean(self):
        self.repo.git.execute(['git', 'checkout', self.current_branch if self.current_branch else self.current_commit])
        self.repo.git.execute(['git', 'branch', '-D', self._workingbranch])
        self._patchmerged = False
