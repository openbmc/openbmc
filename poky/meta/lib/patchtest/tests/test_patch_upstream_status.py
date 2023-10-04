# Checks related to the patch's upstream-status lines
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
import parse_upstream_status
import pyparsing
import os

class PatchUpstreamStatus(base.Base):

    upstream_status_regex = pyparsing.AtLineStart("+" + "Upstream-Status")

    @classmethod
    def setUpClassLocal(cls):
        cls.newpatches = []
        # get just those relevant patches: new software patches
        for patch in cls.patchset:
            if patch.path.endswith('.patch') and patch.is_added_file:
                cls.newpatches.append(patch)

    def setUp(self):
        if self.unidiff_parse_error:
            self.skip('Python-unidiff parse error')
        self.valid_status    = ', '.join(parse_upstream_status.upstream_status_nonliteral_valid_status)
        self.standard_format = 'Upstream-Status: <Valid status>'

    def test_upstream_status_presence_format(self):
        if not PatchUpstreamStatus.newpatches:
            self.skip("There are no new software patches, no reason to test Upstream-Status presence/format")

        for newpatch in PatchUpstreamStatus.newpatches:
            payload = newpatch.__str__()
            if not self.upstream_status_regex.search_string(payload):
                self.fail('Added patch file is missing Upstream-Status in the header',
                          'Add Upstream-Status: <Valid status> to the header of %s' % newpatch.path,
                          data=[('Standard format', self.standard_format), ('Valid status', self.valid_status)])
            for line in payload.splitlines():
                if self.patchmetadata_regex.match(line):
                    continue
                if self.upstream_status_regex.search_string(line):
                        if parse_upstream_status.inappropriate_status_mark.searchString(line):
                            try:
                                parse_upstream_status.upstream_status_inappropriate_info.parseString(line.lstrip('+'))
                            except pyparsing.ParseException as pe:
                                self.fail('Upstream-Status is Inappropriate, but no reason was provided',
                                          'Include a brief reason why %s is inappropriate' % os.path.basename(newpatch.path),
                                          data=[('Current', pe.pstr), ('Standard format', 'Upstream-Status: Inappropriate [reason]')])
                        elif parse_upstream_status.submitted_status_mark.searchString(line):
                            try:
                                parse_upstream_status.upstream_status_submitted_info.parseString(line.lstrip('+'))
                            except pyparsing.ParseException as pe:
                                self.fail('Upstream-Status is Submitted, but it is not mentioned where',
                                          'Include where %s was submitted' % os.path.basename(newpatch.path),
                                          data=[('Current', pe.pstr), ('Standard format', 'Upstream-Status: Submitted [where]')])
                        else:
                            try:
                                parse_upstream_status.upstream_status.parseString(line.lstrip('+'))
                            except pyparsing.ParseException as pe:
                                self.fail('Upstream-Status is in incorrect format',
                                          'Fix Upstream-Status format in %s' % os.path.basename(newpatch.path),
                                          data=[('Current', pe.pstr), ('Standard format', self.standard_format), ('Valid status', self.valid_status)])
