SUMMARY = "Drivers and tools to support ATM networking under Linux"
HOMEPAGE = "http://linux-atm.sourceforge.net/"
SECTION = "libs"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"

DEPENDS = "flex flex-native"

SRC_URI = "http://nchc.dl.sourceforge.net/project/${BPN}/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
           file://link-with-ldflags.patch \
           file://install-from-buildir.patch \
           file://0001-fix-compile-error-with-linux-kernel-v4.8.patch \
           file://0001-ttcp-Add-printf-format-string.patch \
           file://0003-mpoad-Drop-old-hack-to-compile-with-very-old-glibc.patch \
           file://0001-IFNAMSIZ-is-defined-in-net-if.h.patch \
           file://0001-saaldump-atmdump-Include-linux-sockios.h-for-SIOCGST.patch \
           file://0001-make-Add-PREFIX-knob.patch \
           file://0001-include-string-h-from-memcpy-and-strcpy-function-pro.patch \
           file://0001-configure-Check-for-symbol-from-libresolv-instead-of.patch \
           "

SRC_URI:append:libc-musl = " file://musl-no-on_exit.patch"

SRC_URI[sha256sum] = "9645481a2b16476b59220aa2d6bc5bc41043f291326c9b37581018fbd16dd53a"

LIC_FILES_CHKSUM = "\
file://COPYING;md5=d928de9537d846935a98af3bbc6e6ee1 \
file://COPYING.GPL;md5=ac2db169b9309e240555bc77be4f1a33 \
file://COPYING.LGPL;md5=6e29c688d912da12b66b73e32b03d812"

inherit autotools pkgconfig

EXTRA_OEMAKE += "ROOTPREFIX=${root_prefix}"

FILES:${PN} += "${nonarch_base_libdir}/firmware"

# http://errors.yoctoproject.org/Errors/Details/766901/
# linux-atm-2.5.2/src/led/conn.c:414:57: error: passing argument 3 of 'accept' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
