SUMMARY = "simple dynamic multicast routing daemon that only uses IGMP signalling"
HOMEPAGE = "http://sourceforge.net/projects/igmpproxy/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=432040ff3a55670c1dec0c32b209ad69"

SRC_URI = "https://github.com/pali/igmpproxy/releases/download/${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "afa4b75a823b82f71ce99f33eae4e8136b906ae8a5ede5caaad93bac38cdae24"

UPSTREAM_CHECK_URI = "https://github.com/pali/${BPN}/releases"

inherit autotools pkgconfig
