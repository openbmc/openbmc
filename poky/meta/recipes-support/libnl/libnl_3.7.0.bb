SUMMARY = "A library for applications dealing with netlink sockets"
DESCRIPTION = "The libnl suite is a collection of libraries providing \
APIs to netlink protocol based Linux kernel interfaces. libnl is the core \
library implementing the fundamentals required to use the netlink protocol \
such as socket handling, message construction and parsing, and sending \
and receiving of data."
HOMEPAGE = "http://www.infradead.org/~tgr/libnl/"
SECTION = "libs/network"

PE = "1"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "flex-native bison-native"

SRC_URI = "${GITHUB_BASE_URI}/download/${BPN}${@d.getVar('PV').replace('.','_')}/${BP}.tar.gz \
           file://enable-serial-tests.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "9fe43ccbeeea72c653bdcf8c93332583135cda46a79507bfd0a483bb57f65939"

GITHUB_BASE_URI = "https://github.com/thom311/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/libnl(?P<pver>.+)"

inherit autotools pkgconfig ptest github-releases

FILES:${PN} = "${libdir}/libnl-3.so.* \
               ${libdir}/libnl.so.* \
               ${sysconfdir}"
RREPLACES:${PN} = "libnl2"
RCONFLICTS:${PN} = "libnl2"

FILES:${PN}-dev += "${libdir}/libnl/cli/*/*.la"
FILES:${PN}-staticdev += "${libdir}/libnl/cli/*/*.a"

PACKAGES += "${PN}-cli ${PN}-genl ${PN}-idiag ${PN}-nf ${PN}-route ${PN}-xfrm"
FILES:${PN}-cli   = "${libdir}/libnl-cli-3.so.* \
                     ${libdir}/libnl/cli/*/*.so \
                     ${bindir}/genl-ctrl-list \
                     ${bindir}/idiag-socket-details \
                     ${bindir}/nf-* \
                     ${bindir}/nl-*"
FILES:${PN}-genl  = "${libdir}/libnl-genl-3.so.* \
                     ${libdir}/libnl-genl.so.*"
FILES:${PN}-idiag = "${libdir}/libnl-idiag-3.so.*"
FILES:${PN}-nf    = "${libdir}/libnl-nf-3.so.*"
FILES:${PN}-route = "${libdir}/libnl-route-3.so.*"
FILES:${PN}-xfrm  = "${libdir}/libnl-xfrm-3.so.*"
RREPLACES:${PN}-genl = "libnl-genl2"
RCONFLICTS:${PN}-genl = "libnl-genl2"

RDEPENDS:${PN}-ptest += "libcheck"
RRECOMMENDS:${PN}-ptest += "kernel-module-dummy kernel-module-bonding"
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
    # contains build paths
    rm ${D}${PTEST_PATH}/*.la
}

BBCLASSEXTEND = "native nativesdk"
