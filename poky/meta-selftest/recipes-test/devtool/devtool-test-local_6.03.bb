LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "http://downloads.yoctoproject.org/mirror/sources/syslinux-${PV}.tar.xz \
           file://file1 \
           file://file2"

SRC_URI[md5sum] = "92a253df9211e9c20172796ecf388f13"
SRC_URI[sha256sum] = "26d3986d2bea109d5dc0e4f8c4822a459276cf021125e8c9f23c3cca5d8c850e"

S = "${WORKDIR}/syslinux-${PV}"

EXCLUDE_FROM_WORLD = "1"
