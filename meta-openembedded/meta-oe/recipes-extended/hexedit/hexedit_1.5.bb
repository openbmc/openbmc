SUMMARY = "view and edit files in hexadecimal or in ASCII"
HOMEPAGE = "http://rigaux.org/hexedit.html"
SECTION = "console/utils"
LICENSE = "GPLv2+"
DEPENDS = "ncurses"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://github.com/pixel/hexedit.git \
    "

SRCREV = "baf45a289360a39a05253949fb9d1b50e4668d8a"

S = "${WORKDIR}/git"

inherit autotools-brokensep update-alternatives

ALTERNATIVE_${PN} = "hexedit"
ALTERNATIVE_LINK_NAME[hexedit] = "${bindir}/hexedit"

