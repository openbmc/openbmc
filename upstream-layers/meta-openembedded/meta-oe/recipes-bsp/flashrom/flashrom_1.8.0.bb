DESCRIPTION = "flashrom is a utility for identifying, reading, writing, verifying and erasing flash chips"
LICENSE = "GPL-2.0-or-later"
HOMEPAGE = "http://flashrom.org"

LIC_FILES_CHKSUM = "file://COPYING.rst;md5=c0d58714ca15beeaed5b44a38116e133"
SRC_URI = "https://download.flashrom.org/releases/flashrom-v${PV}.tar.xz \
           file://0002-meson-Add-options-pciutils-ftdi-usb.patch \
           "

SRC_URI[sha256sum] = "654c9c61745c250cd3b5ccd0e56fc43ee76980f92a5e078420420639d66975a2"

S = "${UNPACKDIR}/flashrom-v${PV}"

inherit meson pkgconfig

PACKAGECONFIG ??= "pci usb ftdi"
PACKAGECONFIG[pci] = "-Dpciutils=true,-Dpciutils=false,pciutils"
PACKAGECONFIG[usb] = "-Dusb=true,-Dusb=false,libusb"
PACKAGECONFIG[ftdi] = "-Dftdi=true,-Dftdi=false,libftdi"

EXTRA_OEMESON = "-Dbash_completion=disabled -Dtests=disabled"
