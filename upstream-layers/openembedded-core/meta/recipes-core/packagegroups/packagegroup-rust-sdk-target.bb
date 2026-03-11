#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Target packages for the Rust SDK"

inherit packagegroup

RDEPENDS:${PN} = " \
    rust \
    cargo \
"
