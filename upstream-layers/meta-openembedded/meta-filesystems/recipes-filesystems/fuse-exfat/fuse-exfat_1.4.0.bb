SUMMARY = "read and write exFAT driver for FUSE"
DESCRIPTION = "fuse-exfat is a read and write driver implementing the \
extended file allocation table as a filesystem in userspace. A mounthelper \
is provided under the name mount.exfat-fuse. \
"
HOMEPAGE = "https://github.com/relan/exfat"
SECTION = "universe/otherosfs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI = "https://github.com/relan/exfat/releases/download/v${PV}/${BP}.tar.gz"

UPSTREAM_CHECK_URI = "https://github.com/relan/exfat/releases"

DEPENDS = "fuse virtual/libc"
RRECOMMENDS:${PN} = "util-linux-mount"

inherit autotools pkgconfig

SRC_URI[sha256sum] = "a1cfedc55e0e7a12c184605aa0f0bf44b24a3fb272449b20b2c8bbe6edb3001e"

EXTRA_OECONF += "sbindir=${base_sbindir}"
