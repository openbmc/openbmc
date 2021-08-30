SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"
PE = "1"

SRC_URI = "git://salsa.debian.org/md/netbase.git;protocol=https"
SRCREV = "1c892c96a078ef28ec1a94681b3a0da7a3d545f7"

inherit allarch

S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/n/netbase/"

do_install () {
	install -d ${D}${sysconfdir}
	install -m 0644 ${S}/etc/rpc ${D}${sysconfdir}/rpc
	install -m 0644 ${S}/etc/protocols ${D}${sysconfdir}/protocols
	install -m 0644 ${S}/etc/services ${D}${sysconfdir}/services
	install -m 0644 ${S}/etc/ethertypes ${D}${sysconfdir}/ethertypes
}
