SUMMARY = "Bitwise terminal calculator"
DESCRIPTION = "Bitwise is multi base interactive calculator \
supporting dynamic base conversion and bit manipulation.\
It's a handy tool for low level hackers, \
kernel developers and device drivers developers."

HOMEPAGE = "https://github.com/mellowcandle/bitwise"
SECTION = "console/utils"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "https://github.com/mellowcandle/bitwise/releases/download/v${PV}/bitwise-v${PV}.tar.gz \
           file://0001-makefile.am-Fix-build-when-build-dir-is-not-same-as-.patch \
           file://run-ptest \
           file://ptest.out.expected \
           "
SRC_URI[sha256sum] = "806271fa5bf31de0600315e8720004a8f529954480e991ca84a9868dc1cae97e"

UPSTREAM_CHECK_URI = "https://github.com/mellowcandle/bitwise/releases"

S = "${WORKDIR}/${BPN}-v${PV}"

DEPENDS = "ncurses readline"

inherit autotools ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0644 ${WORKDIR}/ptest.out.expected ${D}${PTEST_PATH}/ptest.out.expected
}

