DESCRIPTION = "Network Block Device"
HOMEPAGE = "http://nbd.sourceforge.net"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "glib-2.0"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "98b74c655ed94a66686c5ba19480d98e"
SRC_URI[sha256sum] = "d95c6bb1a3ab33b953af99b73fb4833e123bd25433513b32d57dbeb1a0a0d189"

inherit autotools pkgconfig

PACKAGES = "${PN}-client ${PN}-server ${PN}-dbg ${PN}-trdump ${PN}-doc"

FILES_${PN}-client = "${sbindir}/${BPN}-client"
FILES_${PN}-server = "${bindir}/${BPN}-server"
FILES_${PN}-trdump = "${bindir}/${BPN}-trdump"
