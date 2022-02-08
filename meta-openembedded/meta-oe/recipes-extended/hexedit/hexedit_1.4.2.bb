SUMMARY = "view and edit files in hexadecimal or in ASCII"
HOMEPAGE = "http://rigaux.org/hexedit.html"
SECTION = "console/utils"
LICENSE = "GPLv2+"
DEPENDS = "ncurses"

LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "git://github.com/pixel/hexedit.git;branch=master;protocol=https \
    "

SRCREV = "800e4b2e6280531a84fd23ee0b48e16baeb90878"

S = "${WORKDIR}/git"

inherit autotools-brokensep
