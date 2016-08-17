SUMMARY = "A library for applications dealing with netlink sockets"
HOMEPAGE = "http://www.infradead.org/~tgr/libnl/"
SECTION = "libs/network"

PE = "1"
PR = "r1"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "flex-native bison-native"

SRC_URI = "http://www.infradead.org/~tgr/${BPN}/files/${BP}.tar.gz \
           file://fix-pktloc_syntax_h-race.patch \
           file://fix-pc-file.patch \
          "

SRC_URI[md5sum] = "03f74d0cd5037cadc8cdfa313bbd195c"
SRC_URI[sha256sum] = "8beb7590674957b931de6b7f81c530b85dc7c1ad8fbda015398bc1e8d1ce8ec5"

inherit autotools pkgconfig

FILES_${PN} = "${libdir}/libnl-3.so.* \
               ${libdir}/libnl.so.* \
               ${sysconfdir}"
RREPLACES_${PN} = "libnl2"
RCONFLICTS_${PN} = "libnl2"
FILES_${PN}-staticdev += "${libdir}/libnl/cli/*/*.a"

PACKAGES += "${PN}-cli ${PN}-route ${PN}-nf ${PN}-genl ${PN}-idiag"
FILES_${PN}-cli   = "${libdir}/libnl-cli-3.so.* \
                     ${libdir}/libnl/cli/*/*.so \
                     ${libdir}/libnl/cli/*/*.la \
                     ${sbindir}/nl-*"
FILES_${PN}-route = "${libdir}/libnl-route-3.so.*"
FILES_${PN}-idiag = "${libdir}/libnl-idiag-3.so.*"
FILES_${PN}-nf    = "${libdir}/libnl-nf-3.so.*"
FILES_${PN}-genl  = "${libdir}/libnl-genl-3.so.* \
                     ${libdir}/libnl-genl.so.* \
                     ${sbindir}/genl-ctrl-list"
RREPLACES_${PN}-genl = "libnl-genl2 libnl-genl-3-200"
RCONFLICTS_${PN}-genl = "libnl-genl2 libnl-genl-3-200"
