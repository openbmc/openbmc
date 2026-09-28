SUMMARY = "A fuse filesystem to access the contents of an iPhone or iPod Touch"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"
HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "fuse3 libimobiledevice"

SRC_URI = "https://github.com/libimobiledevice/ifuse/releases/download/${PV}/ifuse-${PV}.tar.bz2"

SRC_URI[sha256sum] = "9d490470ba6553f8052b385bb5330462e46fbe82131ebe65be47a1cc1c70e857"

inherit autotools pkgconfig

EXTRA_OECONF = "PACKAGE_VERSION=${PV}"
