# upstream-status pyparsing definition
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only


import common
import pyparsing

upstream_status_literal_valid_status = ["Pending", "Backport", "Denied", "Inappropriate", "Submitted"]
upstream_status_nonliteral_valid_status = ["Pending", "Backport", "Denied", "Inappropriate [reason]", "Submitted [where]"]

upstream_status_valid_status = pyparsing.Or(
    [pyparsing.Literal(status) for status in upstream_status_literal_valid_status]
)

upstream_status_mark         = pyparsing.Literal("Upstream-Status")
inappropriate_status_mark    = common.inappropriate
submitted_status_mark        = common.submitted

upstream_status              = common.start + upstream_status_mark + common.colon + upstream_status_valid_status
upstream_status_inappropriate_info = common.start + upstream_status_mark + common.colon + common.inappropriateinfo
upstream_status_submitted_info = common.start + upstream_status_mark + common.colon + common.submittedinfo
