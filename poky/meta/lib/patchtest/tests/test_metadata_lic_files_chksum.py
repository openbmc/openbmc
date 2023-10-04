# Checks related to the patch's LIC_FILES_CHKSUM  metadata variable
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
import re
from data import PatchTestInput, PatchTestDataStore

class LicFilesChkSum(base.Metadata):
    metadata = 'LIC_FILES_CHKSUM'
    license  = 'LICENSE'
    closed   = 'CLOSED'
    lictag   = 'License-Update'
    lictag_re  = re.compile("^%s:" % lictag, re.MULTILINE)

    def setUp(self):
        # these tests just make sense on patches that can be merged
        if not PatchTestInput.repo.canbemerged:
            self.skip('Patch cannot be merged')

    def test_lic_files_chksum_presence(self):
        if not self.added:
            self.skip('No added recipes, skipping test')

        for pn in self.added:
            rd = self.tinfoil.parse_recipe(pn)
            pathname = rd.getVar('FILE')
            # we are not interested in images
            if '/images/' in pathname:
                continue
            lic_files_chksum = rd.getVar(self.metadata)
            if rd.getVar(self.license) == self.closed:
                continue
            if not lic_files_chksum:
                self.fail('%s is missing in newly added recipe' % self.metadata,
                          'Specify the variable %s in %s' % (self.metadata, pn))

    def pretest_lic_files_chksum_modified_not_mentioned(self):
        if not self.modified:
            self.skip('No modified recipes, skipping pretest')
        # get the proper metadata values
        for pn in self.modified:
            rd = self.tinfoil.parse_recipe(pn)
            pathname = rd.getVar('FILE')
            # we are not interested in images
            if '/images/' in pathname:
                continue
            PatchTestDataStore['%s-%s-%s' % (self.shortid(),self.metadata,pn)] = rd.getVar(self.metadata)

    def test_lic_files_chksum_modified_not_mentioned(self):
        if not self.modified:
            self.skip('No modified recipes, skipping test')

        # get the proper metadata values
        for pn in self.modified:
            rd = self.tinfoil.parse_recipe(pn)
            pathname = rd.getVar('FILE')
            # we are not interested in images
            if '/images/' in pathname:
                continue
            PatchTestDataStore['%s-%s-%s' % (self.shortid(),self.metadata,pn)] = rd.getVar(self.metadata)
        # compare if there were changes between pre-merge and merge
        for pn in self.modified:
            pretest = PatchTestDataStore['pre%s-%s-%s' % (self.shortid(),self.metadata, pn)]
            test    = PatchTestDataStore['%s-%s-%s' % (self.shortid(),self.metadata, pn)]

            # TODO: this is workaround to avoid false-positives when pretest metadata is empty (not reason found yet)
            # For more info, check bug 12284
            if not pretest:
                return

            if pretest != test:
                # if any patch on the series contain reference on the metadata, fail
                for commit in self.commits:
                    if self.lictag_re.search(commit.commit_message):
                       break
                else:
                    self.fail('LIC_FILES_CHKSUM changed on target %s but there is no "%s" tag in commit message' % (pn, self.lictag),
                              'Include "%s: <description>" into the commit message with a brief description' % self.lictag,
                              data=[('Current checksum', pretest), ('New checksum', test)])
