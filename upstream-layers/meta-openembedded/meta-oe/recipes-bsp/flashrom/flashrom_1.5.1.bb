DESCRIPTION = "flashrom is a utility for identifying, reading, writing, verifying and erasing flash chips"
LICENSE = "GPL-2.0-or-later"
HOMEPAGE = "http://flashrom.org"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SRC_URI = "https://download.flashrom.org/releases/flashrom-v${PV}.tar.xz \
           file://0002-meson-Add-options-pciutils-ftdi-usb.patch \
           "

SRC_URI[sha256sum] = "1f934b076ed49eace203655ec249fc7861a6b8e87fe4aef732e47b6e485b6293"

S = "${UNPACKDIR}/flashrom-v${PV}"

inherit meson pkgconfig

PACKAGECONFIG ??= "pci usb ftdi"
PACKAGECONFIG[pci] = "-Dpciutils=true,-Dpciutils=false,pciutils"
PACKAGECONFIG[usb] = "-Dusb=true,-Dusb=false,libusb"
PACKAGECONFIG[ftdi] = "-Dftdi=true,-Dftdi=false,libftdi"

EXTRA_OEMESON = "-Dbash_completion=disabled -Dtests=disabled"
