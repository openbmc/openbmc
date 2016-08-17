# This package builds tools to manage NIS
# The source package is utils/net/NIS/yp-tools
#
require nis.inc

SUMMARY = "NIS client programs"
DESCRIPTION = " \
Network Information Service tools.  \
This package contains ypcat, ypmatch, ypset, \
ypwhich, yppasswd, domainname, nisdomainname \
and ypdomainname. \
"

PNBLACKLIST[yp-tools] ?= "BROKEN: fails to build for qemuarm."

SRC_URI = "http://www.linux-nis.org/download/yp-tools/${BP}.tar.bz2 \
           file://domainname.service \
           file://yp-tools-ipv4-ipv6-Provide-an-in-place-version-of-mapv4v6addr.patch \
"
SRC_URI[md5sum] = "acebeecc11a73fb8097503670344834c"
SRC_URI[sha256sum] = "812be817df3d4c25813552be336c6c6ad5aedaf65611b81af3ad9f98fb3c2e50"

DEPENDS = "libtirpc"

inherit autotools systemd
SYSTEMD_SERVICE_${PN} = "domainname.service"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"

CACHED_CONFIGUREVARS += "ac_cv_prog_STRIP=/bin/true"

EXTRA_OECONF = " \
                --disable-rpath \
                --libdir=${libdir}/yp-nis/ \
                --includedir=${includedir}/yp-nis/ \
               "

FILES_${PN} += " ${libdir}/yp-nis/*.so.*.* ${libdir}/yp-nis/pkgconfig/"
FILES_${PN}-dbg += " ${libdir}/yp-nis/.debug"
FILES_${PN}-dev += " ${libdir}/yp-nis/*.so ${libdir}/yp-nis/*.so.[12] ${libdir}/yp-nis/*.la"
FILES_${PN}-staticdev += " ${libdir}/yp-nis/*.a"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/domainname.service ${D}${systemd_unitdir}/system
}
