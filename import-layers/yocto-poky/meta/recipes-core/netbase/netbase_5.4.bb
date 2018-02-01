SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"
PE = "1"

SRC_URI = "http://snapshot.debian.org/archive/debian/20170112T093812Z/pool/main/n/${BPN}/${BPN}_${PV}.tar.xz \
           file://netbase-add-rpcbind-as-an-alias-to-sunrpc.patch \
           file://hosts"

SRC_URI[md5sum] = "117cb70c55ef3c1c002f127812b114c1"
SRC_URI[sha256sum] = "66ff73d2d162e2d49db43988d8b8cd328cf7fffca042db73397f14c71825e80d"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/n/netbase/"
do_install () {
	install -d ${D}/${mandir}/man8 ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/hosts ${D}${sysconfdir}/hosts
	install -m 0644 etc-rpc ${D}${sysconfdir}/rpc
	install -m 0644 etc-protocols ${D}${sysconfdir}/protocols
	install -m 0644 etc-services ${D}${sysconfdir}/services
}

CONFFILES_${PN} = "${sysconfdir}/hosts"
