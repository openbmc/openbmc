# common pyparsing variables
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only

import pyparsing

# general
colon = pyparsing.Literal(":")
start = pyparsing.LineStart()
end   = pyparsing.LineEnd()
at = pyparsing.Literal("@")
lessthan = pyparsing.Literal("<")
greaterthan = pyparsing.Literal(">")
opensquare = pyparsing.Literal("[")
closesquare = pyparsing.Literal("]")
inappropriate = pyparsing.CaselessLiteral("Inappropriate")
submitted = pyparsing.CaselessLiteral("Submitted")

# word related
nestexpr = pyparsing.nestedExpr(opener='[', closer=']')
inappropriateinfo = pyparsing.Literal("Inappropriate") + nestexpr
submittedinfo = pyparsing.Literal("Submitted") + nestexpr
word = pyparsing.Word(pyparsing.alphas)
worddot = pyparsing.Word(pyparsing.alphas+".")
