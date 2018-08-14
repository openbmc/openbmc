SUMMARY = "A protocol library to access an iPhone or iPod Touch in Linux"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07 \
    file://COPYING.LESSER;md5=6ab17b41640564434dda85c06b7124f7 \
"

HOMEPAGE ="http://www.libimobiledevice.org/"

DEPENDS = "libplist usbmuxd libtasn1 gnutls libgcrypt"

SRC_URI = " \
    http://www.libimobiledevice.org/downloads/libimobiledevice-${PV}.tar.bz2 \
    file://configure-fix-largefile.patch \
    file://inline-without-definition.patch \
"

SRC_URI[md5sum] = "3f28cbc6a2e30d34685049c0abde5183"
SRC_URI[sha256sum] = "67499cfaa6172f566ee6b0783605acffe484fb7ddc3b09881ab7ac58667ee5b8"

inherit autotools pkgconfig

EXTRA_OECONF = " --without-cython "
