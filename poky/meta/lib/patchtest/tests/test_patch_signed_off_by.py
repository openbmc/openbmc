# Checks related to the patch's signed-off-by lines
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
import parse_signed_off_by
import re

class PatchSignedOffBy(base.Base):

    @classmethod
    def setUpClassLocal(cls):
        cls.newpatches = []
        # get just those relevant patches: new software patches
        for patch in cls.patchset:
            if patch.path.endswith('.patch') and patch.is_added_file:
                cls.newpatches.append(patch)

        cls.mark = str(parse_signed_off_by.signed_off_by_mark).strip('"')

        # match PatchSignedOffBy.mark with '+' preceding it
        cls.prog = parse_signed_off_by.patch_signed_off_by

    def setUp(self):
        if self.unidiff_parse_error:
            self.skip('Parse error %s' % self.unidiff_parse_error)

    def test_signed_off_by_presence(self):
        if not PatchSignedOffBy.newpatches:
            self.skip("There are no new software patches, no reason to test %s presence" % PatchSignedOffBy.mark)

        for newpatch in PatchSignedOffBy.newpatches:
            payload = newpatch.__str__()
            for line in payload.splitlines():
                if self.patchmetadata_regex.match(line):
                    continue
                if PatchSignedOffBy.prog.search_string(payload):
                    break
            else:
                self.fail('A patch file has been added, but does not have a Signed-off-by tag',
                          'Sign off the added patch file (%s)' % newpatch.path)
