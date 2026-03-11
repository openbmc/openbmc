#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit ptest

EXTRA_OECONF:append = " ${@bb.utils.contains('PTEST_ENABLED', '1', '--enable-installed-tests', '--disable-installed-tests', d)}"

FILES:${PN}-ptest += "${libexecdir}/installed-tests/ \
                      ${datadir}/installed-tests/"

RDEPENDS:${PN}-ptest += "gnome-desktop-testing"
