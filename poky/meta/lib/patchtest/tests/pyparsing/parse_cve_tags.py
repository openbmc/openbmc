# signed-off-by pyparsing definition
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only


import pyparsing
import common

name = pyparsing.Regex('\S+.*(?= <)')
username = pyparsing.OneOrMore(common.worddot)
domain = pyparsing.OneOrMore(common.worddot)
cve = pyparsing.Regex('CVE\-\d{4}\-\d+')
cve_mark = pyparsing.Literal("CVE:")

cve_tag = pyparsing.AtLineStart(cve_mark + cve)
patch_cve_tag = pyparsing.AtLineStart("+" + cve_mark + cve)
