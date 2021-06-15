SUMMARY = "read and write exFAT driver for FUSE"
DESCRIPTION = "fuse-exfat is a read and write driver implementing the \
extended file allocation table as a filesystem in userspace. A mounthelper \
is provided under the name mount.exfat-fuse. \
"
HOMEPAGE = "https://github.com/relan/exfat"
SECTION = "universe/otherosfs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI = "https://github.com/relan/exfat/releases/download/v${PV}/${BP}.tar.gz"

UPSTREAM_CHECK_URI = "https://github.com/relan/exfat/releases"

DEPENDS = "fuse virtual/libc"
RRECOMMENDS_${PN} = "util-linux-mount"

inherit autotools pkgconfig

SRC_URI[md5sum] = "846b8c36bfa4684719f9e08e9d3a6bff"
SRC_URI[sha256sum] = "07652136064da5e4d32df5555f88c138ffa4835a23b88a5bae2015f21006e0d3"

EXTRA_OECONF += "sbindir=${base_sbindir}"
