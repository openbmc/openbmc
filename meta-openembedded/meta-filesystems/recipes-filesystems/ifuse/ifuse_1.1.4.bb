SUMMARY = "A fuse filesystem to access the contents of an iPhone or iPod Touch"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"
HOMEPAGE ="http://www.libimobiledevice.org/"

DEPENDS = "fuse libimobiledevice"

SRC_URI = "https://github.com/libimobiledevice/ifuse/releases/download/${PV}/ifuse-${PV}.tar.bz2"

SRC_URI[md5sum] = "cd31fbd0ea945b2ff1e39eac8d198fdd"
SRC_URI[sha256sum] = "3550702ef94b2f5f16c7db91c6b3282b2aed1340665834a03e47458e09d98d87"

inherit autotools pkgconfig
