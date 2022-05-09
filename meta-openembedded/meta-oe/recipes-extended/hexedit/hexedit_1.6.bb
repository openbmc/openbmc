SUMMARY = "view and edit files in hexadecimal or in ASCII"
HOMEPAGE = "http://rigaux.org/hexedit.html"
SECTION = "console/utils"
LICENSE = "GPL-2.0-or-later"
DEPENDS = "ncurses"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://github.com/pixel/hexedit.git;branch=master;protocol=https \
    "

SRCREV = "eab92dcaa34b66bc5182772afc9fda4ac8a27597"

S = "${WORKDIR}/git"

inherit autotools-brokensep update-alternatives

ALTERNATIVE:${PN} = "hexedit"
ALTERNATIVE_LINK_NAME[hexedit] = "${bindir}/hexedit"

