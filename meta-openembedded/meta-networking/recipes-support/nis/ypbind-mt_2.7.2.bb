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
DEPENDS:append:libc-musl = " bsd-headers nss"
RDEPENDS:${PN} += "yp-tools"

# ypbind-mt now provides all the functionality of ypbind
# and is used in place of it.
PROVIDES += "ypbind"

SRC_URI = "https://github.com/thkukuk/ypbind-mt/releases/download/v${PV}/${BPN}-${PV}.tar.xz \
           file://0001-dns_hosts-Fix-build-with-musl.patch \
           file://ypbind.init \
           file://ypbind.service \
           "
SRC_URI[sha256sum] = "064f2f185673c5493df83f6400b799f3a359de56118b6ba37c4327111f2fcd8b"

inherit systemd update-rc.d

SYSTEMD_SERVICE:${PN} = "ypbind.service"
INITSCRIPT_NAME = "ypbind"
INITSCRIPT_PARAMS = "start 44 3 5 . stop 70 0 1 2 6 ."

CACHED_CONFIGUREVARS = "ac_cv_prog_STRIP=/bin/true"

CFLAGS:append = " -I${STAGING_INCDIR}/nss3 -I${STAGING_INCDIR}/nspr"

do_install:append () {
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sysconfdir}/rcS.d

    install -m 0755 ${UNPACKDIR}/ypbind.init ${D}${sysconfdir}/init.d/ypbind

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/ypbind.service ${D}${systemd_unitdir}/system
}

# uses glibc internal APIs e.g. _hostalias
COMPATIBLE_HOST:libc-musl = "null"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
