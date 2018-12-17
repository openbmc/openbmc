SUMMARY = "A protocol library to access an iPhone or iPod Touch in Linux"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07 \
    file://COPYING.LESSER;md5=6ab17b41640564434dda85c06b7124f7 \
"

HOMEPAGE ="http://www.libimobiledevice.org/"

DEPENDS = "libplist usbmuxd libusbmuxd libtasn1 gnutls libgcrypt"

SRCREV = "fb71aeef10488ed7b0e60a1c8a553193301428c0"
PV = "1.2.0+git${SRCPV}"
SRC_URI = "\
    git://github.com/libimobiledevice/libimobiledevice;protocol=https \
    file://configure-fix-largefile.patch \
"

S = "${WORKDIR}/git"
inherit autotools pkgconfig

EXTRA_OECONF = " --without-cython "
