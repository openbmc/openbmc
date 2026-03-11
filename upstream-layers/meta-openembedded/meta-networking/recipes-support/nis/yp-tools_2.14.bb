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
\
This is the final IPv4-only version of yp-tools. \
"

SRC_URI = "http://www.linux-nis.org/download/yp-tools/${BP}.tar.bz2 \
           file://domainname.service \
"
SRC_URI[md5sum] = "ba1f121c17e3ad65368be173b977cd13"
SRC_URI[sha256sum] = "d01f70fadc643a55107a0edc47c6be99d0306bcc4f66df56f65e74238b0124c9"

inherit systemd
SYSTEMD_SERVICE:${PN} = "domainname.service"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"

CACHED_CONFIGUREVARS += "ac_cv_prog_STRIP=/bin/true"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/domainname.service ${D}${systemd_unitdir}/system
}
