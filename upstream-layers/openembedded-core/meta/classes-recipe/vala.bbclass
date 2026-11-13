#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Everyone needs vala-native and targets need vala, too,
# because that is where target builds look for .vapi files.
#
VALADEPENDS = ""
VALADEPENDS:class-target = "vala"
DEPENDS:append = " vala-native ${VALADEPENDS}"

# Upstream Vala >= 0.11 looks in XDG_DATA_DIRS for .vapi files
export XDG_DATA_DIRS = "${STAGING_DATADIR}:${STAGING_LIBDIR}"

# Package additional files
FILES:${PN}-dev += "\
    ${datadir}/vala/vapi/*.vapi \
    ${datadir}/vala/vapi/*.deps \
    ${datadir}/gir-1.0 \
"
