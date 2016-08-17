# This package builds the NIS ypbind daemon
# The source package is utils/net/NIS/ypbind-mt
#
require nis.inc

DESCRIPTION = " \
Multithreaded NIS bind service (ypbind-mt).  \
ypbind-mt is a complete new implementation of a NIS \
binding daemon for Linux. It has the following \
features.  Supports ypbind protocol V1 and V2.  \
Uses threads for better response.  Supports multiple \
domain bindings.  Supports /var/yp/binding/* file \
for Linux libc 4/5 and glibc 2.x.  Supports a list \
of known secure NIS server (/etc/yp.conf) Binds to \
the server which answered as first. \
"
HOMEPAGE = "http://www.linux-nis.org/nis/ypbind-mt/index.html"
DEPENDS = "yp-tools ${@base_contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
PROVIDES += "ypbind"

PNBLACKLIST[ypbind-mt] ?= "BROKEN: Depends on broken yp-tools"

SRC_URI = "http://www.linux-nis.org/download/ypbind-mt/${BP}.tar.bz2 \
           file://ypbind.init \
           file://ypbind.service \
"
SRC_URI[md5sum] = "54e2040d8266ae7d302d081ca310c8a8"
SRC_URI[sha256sum] = "dc2f7d97c94dcab0acfdcd115cd8b464eb8c427e4bb0fe68404ae7465f517cd3"

inherit systemd update-rc.d

SYSTEMD_SERVICE_${PN} = "ypbind.service"
INITSCRIPT_NAME = "ypbind"
INITSCRIPT_PARAMS = "start 44 3 5 . stop 70 0 1 2 6 ."

CACHED_CONFIGUREVARS = "ac_cv_prog_STRIP=/bin/true"

EXTRA_OECONF = "PKG_CONFIG_PATH='${STAGING_LIBDIR}/yp-nis/pkgconfig/'"

do_install_append () {
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sysconfdir}/rcS.d

    install -m 0755 ${WORKDIR}/ypbind.init ${D}${sysconfdir}/init.d/ypbind

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/ypbind.service ${D}${systemd_unitdir}/system
}


RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
