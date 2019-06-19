SUMMARY  = "Platform independent Near Field Communication (NFC) library"
DESCRIPTION = "libnfc is a library which allows userspace application access \
to NFC devices."
LICENSE  = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"
SECTION = "libs"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
SRCREV = "c42e2502d4627d3ea62f83c32677b100bb3cebdc"
SRC_URI = "git://github.com/nfc-tools/libnfc.git \
           file://0001-usbbus-Include-stdint.h-for-uintX_t.patch \
          "

CFLAGS_append_libc-musl = " -D_GNU_SOURCE"
DEPENDS = "libusb"
