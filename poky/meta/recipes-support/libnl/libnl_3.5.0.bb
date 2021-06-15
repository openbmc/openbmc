SUMMARY = "A library for applications dealing with netlink sockets"
DESCRIPTION = "The libnl suite is a collection of libraries providing \
APIs to netlink protocol based Linux kernel interfaces. libnl is the core \
library implementing the fundamentals required to use the netlink protocol \
such as socket handling, message construction and parsing, and sending \
and receiving of data."
HOMEPAGE = "http://www.infradead.org/~tgr/libnl/"
SECTION = "libs/network"

PE = "1"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "flex-native bison-native"

SRC_URI = " \
           https://github.com/thom311/${BPN}/releases/download/${BPN}${@d.getVar('PV').replace('.','_')}/${BP}.tar.gz \
           file://enable-serial-tests.patch \
           file://run-ptest \
          "

SRC_URI[md5sum] = "74ba57b1b1d6f9f92268aa8141d8e8e4"
SRC_URI[sha256sum] = "352133ec9545da76f77e70ccb48c9d7e5324d67f6474744647a7ed382b5e05fa"


UPSTREAM_CHECK_URI = "https://github.com/thom311/${BPN}/releases"

inherit autotools pkgconfig ptest

FILES_${PN} = "${libdir}/libnl-3.so.* \
               ${libdir}/libnl.so.* \
               ${sysconfdir}"
RREPLACES_${PN} = "libnl2"
RCONFLICTS_${PN} = "libnl2"

FILES_${PN}-dev += "${libdir}/libnl/cli/*/*.la"
FILES_${PN}-staticdev += "${libdir}/libnl/cli/*/*.a"

PACKAGES += "${PN}-cli ${PN}-genl ${PN}-idiag ${PN}-nf ${PN}-route ${PN}-xfrm"
FILES_${PN}-cli   = "${libdir}/libnl-cli-3.so.* \
                     ${libdir}/libnl/cli/*/*.so \
                     ${bindir}/genl-ctrl-list \
                     ${bindir}/idiag-socket-details \
                     ${bindir}/nf-* \
                     ${bindir}/nl-*"
FILES_${PN}-genl  = "${libdir}/libnl-genl-3.so.* \
                     ${libdir}/libnl-genl.so.*"
FILES_${PN}-idiag = "${libdir}/libnl-idiag-3.so.*"
FILES_${PN}-nf    = "${libdir}/libnl-nf-3.so.*"
FILES_${PN}-route = "${libdir}/libnl-route-3.so.*"
FILES_${PN}-xfrm  = "${libdir}/libnl-xfrm-3.so.*"
RREPLACES_${PN}-genl = "libnl-genl2"
RCONFLICTS_${PN}-genl = "libnl-genl2"

RDEPENDS_${PN}-ptest += "libcheck"
DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'libcheck', '', d)}"

# make sure the tests don't link against wrong so file
EXTRA_OECONF += "${@bb.utils.contains('PTEST_ENABLED', '1', '--disable-rpath', '', d)}"

do_compile_ptest() {
    # hack to remove the call to `make runtest-TESTS`
    sed -i 's/$(MAKE) $(AM_MAKEFLAGS) runtest-TESTS//g' Makefile
    oe_runmake check
}

do_install_ptest(){
    # legacy? tests are also installed, but ptest-runner will not run them
    # upstream are not running these tests in their CI pipeline
    # issue opened https://github.com/thom311/libnl/issues/270
    install -m 0755 tests/.libs/* ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
