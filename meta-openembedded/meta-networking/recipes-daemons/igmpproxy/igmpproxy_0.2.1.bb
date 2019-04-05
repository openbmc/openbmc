SUMMARY = "simple dynamic multicast routing daemon that only uses IGMP signalling"
HOMEPAGE = "http://sourceforge.net/projects/igmpproxy/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=432040ff3a55670c1dec0c32b209ad69"

SRC_URI = "https://github.com/pali/igmpproxy/releases/download/${PV}/igmpproxy-${PV}.tar.gz \
           file://0001-src-igmpproxy.h-Include-sys-types.h-for-u_short-u_in.patch \
"
SRC_URI[md5sum] = "3a9c2cb42c1f5ee0cb769a4884545641"
SRC_URI[sha256sum] = "d351e623037390f575c1203d9cbb7ba33a8bdef85a3c5e1d2901c5a2a38449a1"

inherit autotools pkgconfig

CFLAGS += "-D_GNU_SOURCE"
