DESCRIPTION = "Network Block Device"
HOMEPAGE = "http://nbd.sourceforge.net"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "glib-2.0"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "910fe6c152f8c30ad8608388e6a4ce89"
SRC_URI[sha256sum] = "e0e1b3538ab7ae5accf56180afd1a9887d415b98d21223b8ad42592b4af7d6cd"

inherit autotools pkgconfig

PACKAGES = "${PN}-client ${PN}-server ${PN}-dbg ${PN}-trdump ${PN}-doc"

FILES_${PN}-client = "${sbindir}/${BPN}-client"
FILES_${PN}-server = "${bindir}/${BPN}-server"
FILES_${PN}-trdump = "${bindir}/${BPN}-trdump"
