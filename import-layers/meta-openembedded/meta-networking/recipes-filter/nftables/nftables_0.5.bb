SUMMARY = "Netfilter Tables userspace utillites"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a78fdd879a263a5e0b42d1fc565e79"
SECTION = "net"

DEPENDS = "libmnl libnftnl readline gmp"
RRECOMMENDS_${PN} += "kernel-module-nf-tables \
    "

SRC_URI = "http://www.netfilter.org/projects/nftables/files/${BP}.tar.bz2 \
           file://fix-to-generate-ntf.8.patch \
          "

SRC_URI[md5sum] = "94bfe1c54bcb9f6ed974835f2fca8069"
SRC_URI[sha256sum] = "1fb6dff333d8a4fc347cbbe273bf905a2634b27a8c39df0d3a45d5a3fde10ad6"

inherit autotools pkgconfig
