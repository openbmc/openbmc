SUMMARY = "read and write exFAT driver for FUSE"
DESCRIPTION = "fuse-exfat is a read and write driver implementing the \
extended file allocation table as a filesystem in userspace. A mounthelper \
is provided under the name mount.exfat-fuse. \
"
HOMEPAGE = "http://code.google.com/p/exfat/"
SECTION = "universe/otherosfs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI = "${DEBIAN_MIRROR}/main/f/fuse-exfat/fuse-exfat_${PV}.orig.tar.gz \
"
DEPENDS = "fuse virtual/libc"
RRECOMMENDS_${PN} = "util-linux-mount"

inherit autotools pkgconfig

SRC_URI[md5sum] = "fca71e6598f79d037a3c7c969cb5710c"
SRC_URI[sha256sum] = "f2e06eba5a21c621aac1d6da21b12a5a324fdd1e20f9c8acd357dd463c2355d9"

S = "${WORKDIR}/${PN}-${PV}"
EXTRA_OECONF += "sbindir=${base_sbindir}"
