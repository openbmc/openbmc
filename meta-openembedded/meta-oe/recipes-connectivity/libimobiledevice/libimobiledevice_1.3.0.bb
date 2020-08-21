SUMMARY = "A protocol library to access an iPhone or iPod Touch in Linux"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07 \
    file://COPYING.LESSER;md5=6ab17b41640564434dda85c06b7124f7 \
"
HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libplist usbmuxd libusbmuxd libtasn1 gnutls libgcrypt"

SRCREV = "15f8652126664e3a4b980e5d1c039b9053ce8566"
SRC_URI = "git://github.com/libimobiledevice/libimobiledevice;protocol=https"

S = "${WORKDIR}/git"
inherit autotools pkgconfig

EXTRA_OECONF = " --without-cython "
