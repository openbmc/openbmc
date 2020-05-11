SUMMARY  = "Platform independent Near Field Communication (NFC) library"
DESCRIPTION = "libnfc is a library which allows userspace application access \
to NFC devices."
LICENSE  = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"
SECTION = "libs"

inherit autotools pkgconfig

PV = "1.7.1+git${SRCPV}"

S = "${WORKDIR}/git"
SRCREV = "2d4543673e9b76c02679ca8b89259659f1afd932"
SRC_URI = "git://github.com/nfc-tools/libnfc.git \
           file://0001-usbbus-Include-stdint.h-for-uintX_t.patch \
          "

CFLAGS_append_libc-musl = " -D_GNU_SOURCE"
DEPENDS = "libusb"
