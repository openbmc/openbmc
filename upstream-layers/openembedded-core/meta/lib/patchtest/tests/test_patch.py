# Checks related to the patch's CVE lines
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import base
import os
import patchtest_patterns
import pyparsing

class TestPatch(base.Base):

    @classmethod
    def setUpClassLocal(cls):
        cls.newpatches = []
        # get just those relevant patches: new software patches
        for patch in cls.patchset:
            if patch.path.endswith('.patch') and patch.is_added_file:
                cls.newpatches.append(patch)

        cls.mark = str(patchtest_patterns.signed_off_by_prefix).strip('"')

        # match PatchSignedOffBy.mark with '+' preceding it
        cls.prog = patchtest_patterns.patch_signed_off_by

    def setUp(self):
        if self.unidiff_parse_error:
            self.skip('Parse error %s' % self.unidiff_parse_error)

        self.valid_status = ", ".join(patchtest_patterns.upstream_status_nonliteral_valid_status)
        self.standard_format = "Upstream-Status: <Valid status>"

        # we are just interested in series that introduce CVE patches, thus discard other
        # possibilities: modification to current CVEs, patch directly introduced into the
        # recipe, upgrades already including the CVE, etc.
        new_cves = [p for p in self.patchset if p.path.endswith('.patch') and p.is_added_file]
        if not new_cves:
            self.skip('No new CVE patches introduced')

    def test_upstream_status_presence_format(self):
        if not TestPatch.newpatches:
            self.skip("There are no new software patches, no reason to test Upstream-Status presence/format")

        for newpatch in TestPatch.newpatches:
            payload = newpatch.__str__()
            if not patchtest_patterns.upstream_status_regex.search_string(payload):
                self.fail(
                    "Added patch file is missing Upstream-Status: <Valid status> in the commit message",
                    data=[
                        ("Standard format", self.standard_format),
                        ("Valid status", self.valid_status),
                    ],
                )
            for line in payload.splitlines():
                if patchtest_patterns.patchmetadata_regex.match(line):
                    continue
                if patchtest_patterns.upstream_status_regex.search_string(line):
                    if patchtest_patterns.inappropriate.searchString(line):
                        try:
                            patchtest_patterns.upstream_status_inappropriate_info.parseString(
                                line.lstrip("+")
                            )
                        except pyparsing.ParseException as pe:
                            self.fail(
                                "Upstream-Status is Inappropriate, but no reason was provided",
                                data=[
                                    ("Current", pe.pstr),
                                    (
                                        "Standard format",
                                        "Upstream-Status: Inappropriate [reason]",
                                    ),
                                ],
                            )
                    elif patchtest_patterns.submitted.searchString(line):
                        try:
                            patchtest_patterns.upstream_status_submitted_info.parseString(
                                line.lstrip("+")
                            )
                        except pyparsing.ParseException as pe:
                            self.fail(
                                "Upstream-Status is Submitted, but it is not mentioned where",
                                data=[
                                    ("Current", pe.pstr),
                                    (
                                        "Standard format",
                                        "Upstream-Status: Submitted [where]",
                                    ),
                                ],
                            )
                    else:
                        try:
                            patchtest_patterns.upstream_status.parseString(line.lstrip("+"))
                        except pyparsing.ParseException as pe:
                            self.fail(
                                "Upstream-Status is in incorrect format",
                                data=[
                                    ("Current", pe.pstr),
                                    ("Standard format", self.standard_format),
                                    ("Valid status", self.valid_status),
                                ],
                            )

    def test_signed_off_by_presence(self):
        if not TestPatch.newpatches:
            self.skip("There are no new software patches, no reason to test %s presence" % PatchSignedOffBy.mark)

        for newpatch in TestPatch.newpatches:
            payload = newpatch.__str__()
            for line in payload.splitlines():
                if patchtest_patterns.patchmetadata_regex.match(line):
                    continue
                if TestPatch.prog.search_string(payload):
                    break
            else:
                self.fail('A patch file has been added without a Signed-off-by tag: \'%s\'' % os.path.basename(newpatch.path))

    def test_cve_tag_format(self):
        for commit in TestPatch.commits:
            if patchtest_patterns.cve.search_string(
                commit.shortlog
            ) or patchtest_patterns.cve.search_string(commit.commit_message):
                tag_found = False
                for line in commit.payload.splitlines():
                    if patchtest_patterns.cve_payload_tag.search_string(line):
                        tag_found = True
                        break
                if not tag_found:
                    self.fail('Missing or incorrectly formatted CVE tag in patch file. Correct or include the CVE tag in the patch with format: "CVE: CVE-YYYY-XXXX"',
                              commit=commit)
