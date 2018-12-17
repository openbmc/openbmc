SUMMARY = "Netfilter Tables userspace utillites"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a78fdd879a263a5e0b42d1fc565e79"

DEPENDS = "libmnl libnftnl readline gmp bison-native"

SRC_URI = "http://www.netfilter.org/projects/nftables/files/${BP}.tar.bz2 \
           "
SRC_URI[md5sum] = "d4dcb61df80aa544b2e142e91d937635"
SRC_URI[sha256sum] = "ad8181b5fcb9ca572f444bed54018749588522ee97e4c21922648bb78d7e7e91"

inherit autotools manpages pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[man] = "--enable--man-doc, --disable-man-doc"

ASNEEDED = ""

RRECOMMENDS_${PN} += "kernel-module-nf-tables"
