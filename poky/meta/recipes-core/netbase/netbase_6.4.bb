SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=78dd2c7c6f487348e4a0092c17a19d42"
PE = "1"

SRC_URI = "${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}.tar.xz"

inherit allarch

SRC_URI[sha256sum] = "fa6621826ff1150e581bd90bc3c8a4ecafe5df90404f207db6dcdf2c75f26ad7"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/n/netbase/"

do_install () {
	install -d ${D}${sysconfdir}
	install -m 0644 ${S}/etc/rpc ${D}${sysconfdir}/rpc
	install -m 0644 ${S}/etc/protocols ${D}${sysconfdir}/protocols
	install -m 0644 ${S}/etc/services ${D}${sysconfdir}/services
	install -m 0644 ${S}/etc/ethertypes ${D}${sysconfdir}/ethertypes
}

S = "${WORKDIR}/netbase"
