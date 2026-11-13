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
           file://run-ptest \
           file://ptest.out.expected \
           "
SRC_URI[sha256sum] = "b8f41f49b9b73ac3abb1e7533a410504f759673fc6e7f35acf56fc82e39cdf37"

UPSTREAM_CHECK_URI = "https://github.com/mellowcandle/bitwise/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

S = "${UNPACKDIR}/${BPN}-v${PV}"

DEPENDS = "ncurses readline"

inherit autotools ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0644 ${UNPACKDIR}/ptest.out.expected ${D}${PTEST_PATH}/ptest.out.expected
}

