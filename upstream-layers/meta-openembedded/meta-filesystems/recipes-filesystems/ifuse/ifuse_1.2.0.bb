SUMMARY = "A fuse filesystem to access the contents of an iPhone or iPod Touch"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"
HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "fuse3 libimobiledevice"

SRC_URI = "https://github.com/libimobiledevice/ifuse/releases/download/${PV}/ifuse-${PV}.tar.bz2"

SRC_URI[sha256sum] = "5c584ae999ed52b386b0d2d1af8f8dcba451cddf31d7cd24367b18db0a9a5a9e"

inherit autotools pkgconfig

EXTRA_OECONF = "PACKAGE_VERSION=${PV}"
