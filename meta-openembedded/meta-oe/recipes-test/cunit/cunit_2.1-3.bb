DESCRIPTION = "CUnit is a C framework for unit testing. Test output supports comandline and GUI results reporting"
HOMEPAGE = "http://cunit.sourceforge.net"
LICENSE = "LGPL-2.0"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYING;md5=7734aa853b85d6f935466f081490ddbb"

S = "${WORKDIR}/CUnit-${PV}"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/cunit/CUnit/${PV}/CUnit-${PV}.tar.bz2 \
           file://fixup-install-docdir.patch \
           file://run-ptest \
          "
SRC_URI[md5sum] = "b5f1a9f6093869c070c6e4a9450cc10c"
SRC_URI[sha256sum] = "f5b29137f845bb08b77ec60584fdb728b4e58f1023e6f249a464efa49a40f214"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/cunit/files/releases"

inherit autotools-brokensep remove-libtool ptest

EXTRA_OECONF = "--enable-memtrace --enable-automated --enable-basic --enable-console"

TESTBIN = "/CUnit/Sources/Test/test_cunit"

PACKAGECONFIG ?= "${@bb.utils.contains('PTEST_ENABLED', '1', 'test','', d)} \
                 "

PACKAGECONFIG[test] = "--enable-test,,,"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	install -m 0755 ${S}${TESTBIN} ${D}${PTEST_PATH}/tests/
}

FILES_${PN}-dev += "${datadir}/CUnit"
FILES_${PN}-doc += "${docdir}"

BBCLASSEXTEND = "native"
