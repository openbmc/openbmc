# subject pyparsing definition
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only

# NOTE:This is an oversimplified syntax of the mbox's summary

import pyparsing
import common

target        = pyparsing.OneOrMore(pyparsing.Word(pyparsing.printables.replace(':','')))
summary       = pyparsing.OneOrMore(pyparsing.Word(pyparsing.printables))
shortlog       = common.start + target + common.colon + summary + common.end
