DESCRIPTION = "Network Block Device user-space tools (TCP version)"
HOMEPAGE = "https://github.com/NetworkBlockDevice/nbd"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "autoconf-archive bison-native glib-2.0 libnl zlib bison-native"

SRC_URI = "git://github.com/NetworkBlockDevice/${BPN}.git;branch=master;protocol=https \
           "
SRCREV = "f96f7fca3b37f4254c26c95f5c6c9dae70e030a1"

inherit autotools pkgconfig

EXTRA_OECONF += "--enable-syslog --enable-lfs --disable-manpages"

PACKAGES = "${PN}-client ${PN}-dbg ${PN}-doc ${PN}-server ${PN}-trdump ${PN}-trplay"

FILES:${PN}-client = "${sbindir}/${BPN}-client"
FILES:${PN}-server = "${bindir}/${BPN}-server"
FILES:${PN}-trdump = "${bindir}/${BPN}-trdump"
FILES:${PN}-trplay = "${bindir}/${BPN}-trplay"
