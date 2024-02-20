SUMMARY  = "Platform independent Near Field Communication (NFC) library"
DESCRIPTION = "libnfc is a library which allows userspace application access \
to NFC devices."
LICENSE  = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"
SECTION = "libs"

inherit autotools pkgconfig

PV = "1.8.0+git"

S = "${WORKDIR}/git"
SRCREV = "f02ff51449240102c27a97173dc495e8e7789046"
SRC_URI = "git://github.com/nfc-tools/libnfc.git;branch=master;protocol=https"

CFLAGS:append:libc-musl = " -D_GNU_SOURCE"
DEPENDS = "libusb"
