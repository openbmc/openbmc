SUMMARY = "Library for low-level interaction with nftables Netlink's API over libmnl"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=79808397c3355f163c012616125c9e26"
SECTION = "libs"
DEPENDS = "libmnl"

SRC_URI = "http://netfilter.org/projects/libnftnl/files/${BP}.tar.bz2  \
          "

SRC_URI[md5sum] = "6d7f9f161538ca7efd535dcc70caf964"
SRC_URI[sha256sum] = "ad3b932a39a1e567308e91b683b32239a5e1aea9b4582dfffe2288c3400ab07e"

inherit autotools pkgconfig
