SUMMARY = "Library for low-level interaction with nftables Netlink's API over libmnl"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=79808397c3355f163c012616125c9e26"
SECTION = "libs"
DEPENDS = "libmnl"

SRC_URI = "http://netfilter.org/projects/libnftnl/files/${BP}.tar.bz2  \
          "

SRC_URI[md5sum] = "af0c62ce6bbd7a7d39def0996c1c17c9"
SRC_URI[sha256sum] = "f6d4f5a702e38bc7987f2363f9fcd65930e8b702595c221a497e2f3a359be497"

inherit autotools pkgconfig
