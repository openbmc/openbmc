DESCRIPTION = "flashrom is a utility for identifying, reading, writing, verifying and erasing flash chips"
LICENSE = "GPL-2.0-or-later"
HOMEPAGE = "http://flashrom.org"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SRC_URI = "https://download.flashrom.org/releases/flashrom-v${PV}.tar.bz2 \
           file://meson-fixes.patch \
           file://0001-flashrom-Mark-RISCV-as-non-memory-mapped-I-O-archite.patch \
           file://0001-hwaccess-use-__asm__-as-is-done-elsewhere.patch \
           "
SRC_URI[sha256sum] = "89a7ff5beb08c89b8795bbd253a51b9453547a864c31793302296b56bbc56d65"

S = "${WORKDIR}/flashrom-v${PV}"

inherit meson pkgconfig

PACKAGECONFIG ??= "pci usb ftdi"
PACKAGECONFIG[pci] = "-Dpciutils=true,-Dpciutils=false,pciutils"
PACKAGECONFIG[usb] = "-Dusb=true,-Dusb=false,libusb"
PACKAGECONFIG[ftdi] = "-Dftdi=true,-Dftdi=false,libftdi"
