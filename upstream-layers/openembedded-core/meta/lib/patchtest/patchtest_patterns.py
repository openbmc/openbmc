# common pyparsing variables
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only

import pyparsing
import re

# general
colon = pyparsing.Literal(":")
line_start = pyparsing.LineStart()
line_end = pyparsing.LineEnd()
lessthan = pyparsing.Literal("<")
greaterthan = pyparsing.Literal(">")
inappropriate = pyparsing.CaselessLiteral("Inappropriate")
submitted = pyparsing.CaselessLiteral("Submitted")

# word related
nestexpr = pyparsing.nestedExpr(opener='[', closer=']')
inappropriateinfo = pyparsing.Literal("Inappropriate") + nestexpr
submittedinfo = pyparsing.Literal("Submitted") + nestexpr
word = pyparsing.Word(pyparsing.alphas)
worddot = pyparsing.Word(pyparsing.alphas+".")

# metadata

metadata_lic = 'LICENSE'
invalid_license = 'PATCHTESTINVALID'
metadata_chksum = 'LIC_FILES_CHKSUM'
license_var  = 'LICENSE'
closed   = 'CLOSED'
lictag_re  = pyparsing.AtLineStart("License-Update:")
lic_chksum_added = pyparsing.AtLineStart("+" + metadata_chksum)
lic_chksum_removed = pyparsing.AtLineStart("-" + metadata_chksum)
add_mark = pyparsing.Regex('\\+ ')
patch_max_line_length = 200
metadata_src_uri = "SRC_URI"
metadata_summary = "SUMMARY"
cve_check_ignore_var = "CVE_CHECK_IGNORE"
cve_status_var = "CVE_STATUS"
endcommit_messages_regex = re.compile(
    r"\(From \w+-\w+ rev:|(?<!\S)Signed-off-by|(?<!\S)---\n"
)
patchmetadata_regex = re.compile(
    r"-{3} \S+|\+{3} \S+|@{2} -\d+,\d+ \+\d+,\d+ @{2} \S+"
)

# mbox
auh_email = 'auh@yoctoproject.org'

invalid_submitters = [pyparsing.Regex("^Upgrade Helper.+"),
            pyparsing.Regex(auh_email),
            pyparsing.Regex("uh@not\.set"),
            pyparsing.Regex("\S+@example\.com")]

mbox_bugzilla = pyparsing.Regex('\[\s?YOCTO.*\]')
mbox_bugzilla_validation = pyparsing.Regex('\[(\s?YOCTO\s?#\s?(\d+)\s?,?)+\]')
mbox_revert_shortlog_regex = pyparsing.Regex('Revert\s+".*"')
mbox_shortlog_maxlength = 90
# based on https://stackoverflow.com/questions/30281026/regex-parsing-github-usernames-javascript
mbox_github_username = pyparsing.Regex('\B@([a-z0-9](?:-(?=[a-z0-9])|[a-z0-9]){0,38}(?<=[a-z0-9]))')

# patch

cve = pyparsing.Regex("CVE\-\d{4}\-\d+")
cve_payload_tag = pyparsing.Regex("\+CVE:(\s+CVE\-\d{4}\-\d+)+")
upstream_status_regex = pyparsing.AtLineStart("+" + "Upstream-Status")

# shortlog

shortlog_target = pyparsing.OneOrMore(pyparsing.Word(pyparsing.printables.replace(':','')))
shortlog_summary = pyparsing.OneOrMore(pyparsing.Word(pyparsing.printables))
shortlog = line_start + shortlog_target + colon + shortlog_summary + line_end

# signed-off-bys

email_pattern = pyparsing.Regex(r"(?P<user>[A-Za-z0-9._%+-]+)@(?P<hostname>[A-Za-z0-9.-]+)\.(?P<domain>[A-Za-z]{2,})")

signed_off_by_prefix = pyparsing.Literal("Signed-off-by:")
signed_off_by_name = pyparsing.Regex('\S+.*(?= <)')
signed_off_by_email = lessthan + email_pattern + greaterthan
signed_off_by = pyparsing.AtLineStart(signed_off_by_prefix + signed_off_by_name + signed_off_by_email)
patch_signed_off_by = pyparsing.AtLineStart("+" + signed_off_by_prefix + signed_off_by_name + signed_off_by_email)

# upstream-status

upstream_status_literal_valid_status = ["Pending", "Backport", "Denied", "Inappropriate", "Submitted", "Inactive-Upstream"]
upstream_status_nonliteral_valid_status = ["Pending", "Backport", "Denied", "Inappropriate [reason]", "Submitted [where]", "Inactive-Upstream [lastcommit: when (and/or) lastrelease: when]"]

upstream_status_valid_status = pyparsing.Or(
    [pyparsing.Literal(status) for status in upstream_status_literal_valid_status]
)

upstream_status_prefix = pyparsing.Literal("Upstream-Status")
upstream_status = line_start + upstream_status_prefix + colon + upstream_status_valid_status
upstream_status_inappropriate_info = line_start + upstream_status_prefix + colon + inappropriateinfo
upstream_status_submitted_info = line_start + upstream_status_prefix + colon + submittedinfo
