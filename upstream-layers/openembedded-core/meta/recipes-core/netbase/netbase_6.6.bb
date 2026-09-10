SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=03a03b9cf1aa619e30b1c9d53141163e"
PE = "1"

SRC_URI = "${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}.tar.xz"

S = "${UNPACKDIR}/work"

inherit allarch

SRC_URI[sha256sum] = "31f7e8a37f3f07010e484f74bcbee503923b38d1a36c6a11326b804acc07224e"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/n/netbase/"

do_install () {
	install -d ${D}${sysconfdir}
	install -m 0644 ${S}/etc/rpc ${D}${sysconfdir}/rpc
	install -m 0644 ${S}/etc/protocols ${D}${sysconfdir}/protocols
	install -m 0644 ${S}/etc/services ${D}${sysconfdir}/services
	install -m 0644 ${S}/etc/ethertypes ${D}${sysconfdir}/ethertypes
}
