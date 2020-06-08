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
\
This is the final IPv4-only version of ypbind-mt. \
"
HOMEPAGE = "https://github.com/thkukuk/ypbind-mt/"
DEPENDS = " \
           yp-tools \
           ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
          "
DEPENDS_append_libc-musl = " bsd-headers nss"
RDEPENDS_${PN} += "yp-tools"

# ypbind-mt now provides all the functionality of ypbind
# and is used in place of it.
PROVIDES += "ypbind"

SRC_URI = "https://github.com/thkukuk/ypbind-mt/releases/download/v${PV}/${BPN}-${PV}.tar.xz \
           file://ypbind.init \
           file://ypbind.service \
           file://0001-dns_hosts-Fix-build-with-musl.patch \
           file://0001-Add-configure-time-check-for-gettid-API.patch \
           "
SRC_URI[md5sum] = "7cf89641fdc128d0919207e4b7caaf1d"
SRC_URI[sha256sum] = "0696c0263c4fd48a4ff2ce6c109f05f37aab0f71646d81cb22c7c28591bf80eb"

inherit systemd update-rc.d

SYSTEMD_SERVICE_${PN} = "ypbind.service"
INITSCRIPT_NAME = "ypbind"
INITSCRIPT_PARAMS = "start 44 3 5 . stop 70 0 1 2 6 ."

CACHED_CONFIGUREVARS = "ac_cv_prog_STRIP=/bin/true"

CFLAGS_append = " -I${STAGING_INCDIR}/nss3 -I${STAGING_INCDIR}/nspr"

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
