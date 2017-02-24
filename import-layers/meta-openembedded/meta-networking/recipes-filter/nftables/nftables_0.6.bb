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
SRC_URI[md5sum] = "fd320e35fdf14b7be795492189b13dae"
SRC_URI[sha256sum] = "dede62655f1c56f2bc9f9be12d103d930dcef6d5f9e0855854ad9c6f93cd6c2d"

inherit autotools pkgconfig
