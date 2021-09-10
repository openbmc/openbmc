SUMMARY = "simple dynamic multicast routing daemon that only uses IGMP signalling"
HOMEPAGE = "http://sourceforge.net/projects/igmpproxy/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=432040ff3a55670c1dec0c32b209ad69"

SRC_URI = "https://github.com/pali/igmpproxy/releases/download/${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "5565874d9631103109a72452cecb5ce7"
SRC_URI[sha256sum] = "d1fc244cb2fbbf99f720bda3e841fe59ece9b6919073790b4b892739b1b844eb"

UPSTREAM_CHECK_URI = "https://github.com/pali/${BPN}/releases"

inherit autotools pkgconfig
