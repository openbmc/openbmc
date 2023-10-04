# Checks related to the patch's SRC_URI metadata variable
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import subprocess
import base
import re
import os
from data import PatchTestInput, PatchTestDataStore

class SrcUri(base.Metadata):

    metadata  = 'SRC_URI'
    md5sum    = 'md5sum'
    sha256sum = 'sha256sum'
    git_regex = re.compile('^git\:\/\/.*')

    def setUp(self):
        # these tests just make sense on patches that can be merged
        if not PatchTestInput.repo.canbemerged:
            self.skip('Patch cannot be merged')

    def pretest_src_uri_left_files(self):
        if not self.modified:
            self.skip('No modified recipes, skipping pretest')

        # get the proper metadata values
        for pn in self.modified:
            # we are not interested in images
            if 'core-image' in pn:
                continue
            rd = self.tinfoil.parse_recipe(pn)
            PatchTestDataStore['%s-%s-%s' % (self.shortid(), self.metadata, pn)] = rd.getVar(self.metadata)

    def test_src_uri_left_files(self):
        if not self.modified:
            self.skip('No modified recipes, skipping pretest')

        # get the proper metadata values
        for pn in self.modified:
            # we are not interested in images
            if 'core-image' in pn:
                continue
            rd = self.tinfoil.parse_recipe(pn)
            PatchTestDataStore['%s-%s-%s' % (self.shortid(), self.metadata, pn)] = rd.getVar(self.metadata)

        for pn in self.modified:
            pretest_src_uri = PatchTestDataStore['pre%s-%s-%s' % (self.shortid(), self.metadata, pn)].split()
            test_src_uri    = PatchTestDataStore['%s-%s-%s' % (self.shortid(), self.metadata, pn)].split()

            pretest_files = set([os.path.basename(patch) for patch in pretest_src_uri if patch.startswith('file://')])
            test_files    = set([os.path.basename(patch) for patch in test_src_uri    if patch.startswith('file://')])

            # check if files were removed
            if len(test_files) < len(pretest_files):

                # get removals from patchset
                filesremoved_from_patchset = set()
                for patch in self.patchset:
                    if patch.is_removed_file:
                        filesremoved_from_patchset.add(os.path.basename(patch.path))

                # get the deleted files from the SRC_URI
                filesremoved_from_usr_uri = pretest_files - test_files

                # finally, get those patches removed at SRC_URI and not removed from the patchset
                # TODO: we are not taking into account  renames, so test may raise false positives
                not_removed = filesremoved_from_usr_uri - filesremoved_from_patchset
                if not_removed:
                    self.fail('Patches not removed from tree',
                              'Amend the patch containing the software patch file removal',
                              data=[('Patch', f) for f in not_removed])

