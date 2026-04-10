SUMMARY = "daemon that sends updates when your IP changes"
HOMEPAGE = "http://sourceforge.net/projects/ez-ipupdate/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7783169b4be06b54e86730eb01bc3a31"

SRC_URI = "${DEBIAN_MIRROR}/main/e/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://Makefile.am.patch \
           file://wformat.patch \
           file://0001-ez-ipupdate-Include-time.h-for-time-API-prototype.patch \
           file://CVE-2003-0887.patch \
		   file://0001-configure-Check-for-string.h-system-header.patch \
           "
SRC_URI[sha256sum] = "bf5b8d11ffe055c5891d0ab64bbfa86e99cbda645d40f346146b939fec8d962d"

inherit autotools pkgconfig

#do_install:append(){
#    install -m 0744 -d ${D}${localstatedir}/lib/ez-ipupdate
#}

#FILES:${PN} += "${localstatedir}/lib/ez-ipupdate"
