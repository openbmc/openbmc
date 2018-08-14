SUMMARY = "daemon that sends updates when your IP changes"
HOMEPAGE = "http://sourceforge.net/projects/ez-ipupdate/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=7783169b4be06b54e86730eb01bc3a31"

SRC_URI = "http://sourceforge.net/projects/ez-ipupdate/files/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
    file://Makefile.am.patch \
    file://cache_file.c.patch \
    file://conf_file.c.patch \
    file://wformat.patch \
    "
SRC_URI[md5sum] = "525be4550b4461fdf105aed8e753b020"
SRC_URI[sha256sum] = "a15ec0dc0b78ec7578360987c68e43a67bc8d3591cbf528a323588830ae22c20"

inherit autotools pkgconfig
