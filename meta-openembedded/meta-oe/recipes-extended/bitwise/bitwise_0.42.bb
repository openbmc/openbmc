SUMMARY = "Bitwise terminal calculator"
DESCRIPTION = "Bitwise is multi base interactive calculator \
supporting dynamic base conversion and bit manipulation.\
It's a handy tool for low level hackers, \
kernel developers and device drivers developers."

HOMEPAGE = "https://github.com/mellowcandle/bitwise"
SECTION = "console/utils"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "https://github.com/mellowcandle/bitwise/releases/download/v${PV}/bitwise-v${PV}.tar.gz \
           file://0001-makefile.am-Fix-build-when-build-dir-is-not-same-as-.patch \
           "
SRC_URI[sha256sum] = "d3d43cef47bf8f49e85f7ed381c3eaf1df921ca51805e0962f1a97a517e1d1d2"

UPSTREAM_CHECK_URI = "https://github.com/mellowcandle/bitwise/releases"

S = "${WORKDIR}/${BPN}-v${PV}"

DEPENDS = "ncurses readline"

inherit autotools
