SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"
PE = "2"

SRC_URI = "${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}~bpo10+1.tar.xz"
S = "${WORKDIR}/${BPN}-${PV}~bpo10+1"

SRC_URI[md5sum] = "4fa7517285b4045ac0dc8dbf6730dd7a"
SRC_URI[sha256sum] = "4e9c3082dff8896cb6b6bea9bb2200d82fb0d7c8d8c8fc9b18704fe553316237"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/n/netbase/"
do_install () {
	install -d ${D}/${mandir}/man8 ${D}${sysconfdir}
	install -m 0644 ${S}/etc/rpc ${D}${sysconfdir}/rpc
	install -m 0644 ${S}/etc/protocols ${D}${sysconfdir}/protocols
	install -m 0644 ${S}/etc/services ${D}${sysconfdir}/services
}
