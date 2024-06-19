DESCRIPTION = "Network Block Device user-space tools (TCP version)"
HOMEPAGE = "https://github.com/NetworkBlockDevice/nbd"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "autoconf-archive bison-native glib-2.0 libnl zlib bison-native"

SRC_URI = "https://github.com/NetworkBlockDevice/${BPN}/releases/download/${BP}/${BP}.tar.xz \
           file://f0418b0d8b54c21a1e5b0c6dce3277e938d07e7c.patch \
           file://0001-nbd-client-Fix-build-on-musl-gcc14.patch \
           "
SRC_URI[sha256sum] = "f0cf509fa5b20b1a07f7904eb637e9b47d3e30b6ed6f00075af5d8b701c78fef"

inherit autotools pkgconfig

EXTRA_OECONF += "--enable-syslog --enable-lfs --disable-manpages"

PACKAGES = "${PN}-client ${PN}-dbg ${PN}-doc ${PN}-server ${PN}-trdump ${PN}-trplay"

FILES:${PN}-client = "${sbindir}/${BPN}-client"
FILES:${PN}-server = "${bindir}/${BPN}-server"
FILES:${PN}-trdump = "${bindir}/${BPN}-trdump"
FILES:${PN}-trplay = "${bindir}/${BPN}-trplay"
