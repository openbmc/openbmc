# Checks related to the patch's summary metadata variable
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
from data import PatchTestInput

class Summary(base.Metadata):
    metadata = 'SUMMARY'

    def setUp(self):
        # these tests just make sense on patches that can be merged
        if not PatchTestInput.repo.canbemerged:
            self.skip('Patch cannot be merged')

    def test_summary_presence(self):
        if not self.added:
            self.skip('No added recipes, skipping test')

        for pn in self.added:
            # we are not interested in images
            if 'core-image' in pn:
                continue
            rd = self.tinfoil.parse_recipe(pn)
            summary = rd.getVar(self.metadata)

            # "${PN} version ${PN}-${PR}" is the default, so fail if default
            if summary.startswith('%s version' % pn):
                self.fail('%s is missing in newly added recipe' % self.metadata,
                          'Specify the variable %s in %s' % (self.metadata, pn))
