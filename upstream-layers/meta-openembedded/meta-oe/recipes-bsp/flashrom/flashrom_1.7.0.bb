DESCRIPTION = "flashrom is a utility for identifying, reading, writing, verifying and erasing flash chips"
LICENSE = "GPL-2.0-or-later"
HOMEPAGE = "http://flashrom.org"

LIC_FILES_CHKSUM = "file://COPYING.rst;md5=c0d58714ca15beeaed5b44a38116e133"
SRC_URI = "https://download.flashrom.org/releases/flashrom-v${PV}.tar.xz \
           file://0002-meson-Add-options-pciutils-ftdi-usb.patch \
           file://0003-cli_classic-Initialize-time_start-time_end.patch \
           "

SRC_URI[sha256sum] = "4328ace9833f7efe7c334bdd73482cde8286819826cc00149e83fba96bf3ab4f"

S = "${UNPACKDIR}/flashrom-v${PV}"

inherit meson pkgconfig

PACKAGECONFIG ??= "pci usb ftdi"
PACKAGECONFIG[pci] = "-Dpciutils=true,-Dpciutils=false,pciutils"
PACKAGECONFIG[usb] = "-Dusb=true,-Dusb=false,libusb"
PACKAGECONFIG[ftdi] = "-Dftdi=true,-Dftdi=false,libftdi"

EXTRA_OEMESON = "-Dbash_completion=disabled -Dtests=disabled"
