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

SRC_URI = "http://www.linux-nis.org/download/yp-tools/${BP}.tar.bz2 \
           file://domainname.service \
           file://0001-ypbind3_binding_dup.c-Include-string.h-for-strdup-de.patch \
           file://0002-yp_dump_bindings.c-Include-string.h-for-memset.patch \
           "
SRC_URI[md5sum] = "fb4a8bffb44ae5d3390351d67f320ef3"
SRC_URI[sha256sum] = "137f19a986382b275bf4a2b1a69eb26689d6f4ac056ddaa21784d6b80eb98faa"

DEPENDS = "libtirpc libnsl2"

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
