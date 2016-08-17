SUMMARY = "A fuse filesystem to access the contents of an iPhone or iPod Touch"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"
HOMEPAGE ="http://www.libimobiledevice.org/"

DEPENDS = "fuse libimobiledevice"

SRC_URI = " \
    http://www.libimobiledevice.org/downloads/ifuse-${PV}.tar.bz2 \
"

SRC_URI[md5sum] = "4152526b2ac3c505cb41797d997be14d"
SRC_URI[sha256sum] = "47835c8afb72588b3202fe0b206d7ea37a68663d9aa4eaf73f0a4bcb6215fc05"

inherit autotools pkgconfig
