DESCRIPTION = "Network Block Device"
HOMEPAGE = "http://nbd.sourceforge.net"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "autoconf-archive bison-native glib-2.0 libnl"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "a6d9e7bbc311a2ed07ef84a58b82b5dd"
SRC_URI[sha256sum] = "6877156d23a7b33f75eee89d2f5c2c91c542afc3cdcb636dea5a88539a58d10c"

inherit autotools pkgconfig

PACKAGES = "${PN}-client ${PN}-dbg ${PN}-doc ${PN}-server ${PN}-trdump ${PN}-trplay"

FILES:${PN}-client = "${sbindir}/${BPN}-client"
FILES:${PN}-server = "${bindir}/${BPN}-server"
FILES:${PN}-trdump = "${bindir}/${BPN}-trdump"
FILES:${PN}-trplay = "${bindir}/${BPN}-trplay"
