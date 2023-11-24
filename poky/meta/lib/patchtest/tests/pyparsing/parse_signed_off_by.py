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

# taken from https://pyparsing-public.wikispaces.com/Helpful+Expressions
email = pyparsing.Regex(r"(?P<user>[A-Za-z0-9._%+-]+)@(?P<hostname>[A-Za-z0-9.-]+)\.(?P<domain>[A-Za-z]{2,})")

email_enclosed = common.lessthan + email + common.greaterthan

signed_off_by_mark = pyparsing.Literal("Signed-off-by:")
signed_off_by = pyparsing.AtLineStart(signed_off_by_mark + name + email_enclosed)
patch_signed_off_by = pyparsing.AtLineStart("+" + signed_off_by_mark + name + email_enclosed)
