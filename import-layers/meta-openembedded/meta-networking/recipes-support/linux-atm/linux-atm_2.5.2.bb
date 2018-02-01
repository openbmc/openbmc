SUMMARY = "Drivers and tools to support ATM networking under Linux"
HOMEPAGE = "http://linux-atm.sourceforge.net/"
SECTION = "libs"
LICENSE = "GPL-2.0 & LGPL-2.0"

DEPENDS = "virtual/kernel flex flex-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "http://nchc.dl.sourceforge.net/project/${BPN}/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
           file://link-with-ldflags.patch \
           file://install-from-buildir.patch \
           file://0001-fix-compile-error-with-linux-kernel-v4.8.patch \
           file://0001-ttcp-Add-printf-format-string.patch \
           file://0002-sigd-Replace-on_exit-API-with-atexit.patch \
           file://0003-mpoad-Drop-old-hack-to-compile-with-very-old-glibc.patch \
"

SRC_URI[md5sum] = "d49499368c3cf15f73a05d9bce8824a8"
SRC_URI[sha256sum] = "9645481a2b16476b59220aa2d6bc5bc41043f291326c9b37581018fbd16dd53a"

LIC_FILES_CHKSUM = "\
file://COPYING;md5=d928de9537d846935a98af3bbc6e6ee1 \
file://COPYING.GPL;md5=ac2db169b9309e240555bc77be4f1a33 \
file://COPYING.LGPL;md5=6e29c688d912da12b66b73e32b03d812"

inherit autotools pkgconfig

# The firmware is explicitly put under /lib when installed.
#

FILES_${PN} += "/lib/firmware"
