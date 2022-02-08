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

# v4.2.3
SRCREV = "1bfda29c342a81b97cb1995ffd9e8da5de63e7ab"

SRC_URI = "git://github.com/thkukuk/yp-tools;branch=master;protocol=https \
           file://domainname.service \
           "

S = "${WORKDIR}/git"

DEPENDS = "libtirpc libnsl2 virtual/crypt"

inherit autotools systemd
SYSTEMD_SERVICE_${PN} = "domainname.service"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"

CACHED_CONFIGUREVARS += "ac_cv_prog_STRIP=/bin/true"

EXTRA_OECONF = " \
                --disable-rpath --disable-domainname \
               "
CFLAGS_append_libc-musl = " -Wno-error=cpp"

FILES_${PN} += " ${libdir}/yp-nis/*.so.*.* ${libdir}/yp-nis/pkgconfig/"
FILES_${PN}-dbg += " ${libdir}/yp-nis/.debug"
FILES_${PN}-dev += " ${libdir}/yp-nis/*.so ${libdir}/yp-nis/*.so.[12] ${libdir}/yp-nis/*.la"
FILES_${PN}-staticdev += " ${libdir}/yp-nis/*.a"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/domainname.service ${D}${systemd_unitdir}/system
}
