SUMMARY = "Library for low-level interaction with nftables Netlink's API over libmnl"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=79808397c3355f163c012616125c9e26"
SECTION = "libs"

SRC_URI = "git://git.netfilter.org/libnftnl;branch=master \
           file://0001-configure.ac-Add-serial-tests.patch \
           file://run-ptest \
           "
SRCREV = "83dd4dc316b4189d16ead54cd30bfc89e5160cfd"

S = "${WORKDIR}/git"

inherit autotools pkgconfig ptest

DEPENDS = "libmnl"
RDEPENDS:${PN}-ptest += " bash python3-core make"

TESTDIR = "tests"

do_compile_ptest() {
    cp -rf ${S}/build-aux .
    oe_runmake buildtest-TESTS
}

do_install_ptest() {
    cp -rf ${B}/build-aux ${D}${PTEST_PATH}
    install -d ${D}${PTEST_PATH}/${TESTDIR}
    cp -rf ${B}/${TESTDIR}/Makefile ${D}${PTEST_PATH}/${TESTDIR}

    # the binaries compiled in ${TESTDIR} will look for a compiler to
    # use, which will cause failures. Substitute the binaries in
    # ${TESTDIR}/.libs instead
    cp -rf ${B}/${TESTDIR}/.libs/* ${D}${PTEST_PATH}/${TESTDIR}

    # Alter the Makefile so that it does not try and rebuild anything in
    # other nonexistent paths before running the actual tests
    sed -i 's/^Makefile/_Makefile/'  ${D}${PTEST_PATH}/${TESTDIR}/Makefile
}
