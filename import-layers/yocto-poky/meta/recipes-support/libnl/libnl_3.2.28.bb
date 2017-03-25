SUMMARY = "A library for applications dealing with netlink sockets"
HOMEPAGE = "http://www.infradead.org/~tgr/libnl/"
SECTION = "libs/network"

PE = "1"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "flex-native bison-native"

SRC_URI = "https://github.com/thom311/${BPN}/releases/download/${BPN}${@d.getVar('PV', True).replace('.','_')}/${BP}.tar.gz \
           file://fix-pktloc_syntax_h-race.patch \
           file://fix-pc-file.patch \
           file://0001-lib-add-utility-function-nl_strerror_l.patch \
           file://0002-lib-switch-to-using-strerror_l-instead-of-strerror_r.patch \
           file://0003-src-switch-to-using-strerror_l-instead-of-strerror_r.patch \
"
UPSTREAM_CHECK_URI = "https://github.com/thom311/${BPN}/releases"

SRC_URI[md5sum] = "bab12db1eb94a42129f712a44be91a67"
SRC_URI[sha256sum] = "cd608992c656e8f6e3ab6c1391b162a5a51c49336b9219f7f390e61fc5437c41"

inherit autotools pkgconfig

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
