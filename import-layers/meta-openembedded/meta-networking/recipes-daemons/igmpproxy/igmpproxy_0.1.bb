SUMMARY = "simple dynamic multicast routing daemon that only uses IGMP signalling"
HOMEPAGE = "http://sourceforge.net/projects/igmpproxy/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=1e995e2799bb0d27d63069b97f805420"

SRC_URI = "http://sourceforge.net/projects/igmpproxy/files/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-src-igmpproxy.h-Include-sys-types.h-for-u_short-u_in.patch \
           "

SRC_URI[md5sum] = "c56f41ec195bc1fe016369bf74efc5a1"
SRC_URI[sha256sum] = "ee18ff3d8c3ae3a29dccb7e5eedf332337330020168bd95a11cece8d7d7ee6ae"

inherit autotools pkgconfig

CFLAGS += "-D_GNU_SOURCE"
