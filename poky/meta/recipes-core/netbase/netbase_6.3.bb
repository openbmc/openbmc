SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"
PE = "1"

SRC_URI = "${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}.tar.xz"

inherit allarch

SRC_URI[sha256sum] = "7c42a6a1cafa0c64103c71cab6431fc8613179b2449a1a00e55e3584e860d81c"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/n/netbase/"

do_install () {
	install -d ${D}${sysconfdir}
	install -m 0644 ${S}/etc/rpc ${D}${sysconfdir}/rpc
	install -m 0644 ${S}/etc/protocols ${D}${sysconfdir}/protocols
	install -m 0644 ${S}/etc/services ${D}${sysconfdir}/services
	install -m 0644 ${S}/etc/ethertypes ${D}${sysconfdir}/ethertypes
}
