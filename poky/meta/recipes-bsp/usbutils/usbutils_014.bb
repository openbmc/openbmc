SUMMARY = "Host side USB console utilities"
DESCRIPTION = "Contains the lsusb utility for inspecting the devices connected to the USB bus."
HOMEPAGE = "http://www.linux-usb.org"
SECTION = "base"

LICENSE = "GPL-2.0-or-later & (GPL-2.0-only | GPL-3.0-only)"
# License files went missing in 010, when 011 is released add LICENSES/* back
LIC_FILES_CHKSUM = "file://lsusb.c;endline=1;md5=7226e442a172bcf25807246d7ef1eba1 \
                    file://lsusb.py.in;beginline=2;endline=2;md5=c443ada211d701156e42ea36d41625b3 \
                    "

DEPENDS = "libusb1 virtual/libiconv udev"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/usb/usbutils/usbutils-${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "59398ab012888dfe0fd12e447b45f36801e9d7b71d9a865fc38e2f549afdb9d0"

inherit autotools pkgconfig update-alternatives

ALTERNATIVE:${PN} = "lsusb"
ALTERNATIVE_PRIORITY = "100"

# The binaries are mostly GPL-2.0-or-later apart from lsusb.py which is
# GPL-2.0-only or GPL-3.0-only.
LICENSE:${PN} = "GPL-2.0-or-later"
LICENSE:${PN}-python = "GPL-2.0-only | GPL-3.0-only"

RRECOMMENDS:${PN} = "udev-hwdb"

PACKAGE_BEFORE_PN =+ "${PN}-python"
FILES:${PN}-python += "${bindir}/lsusb.py"
RDEPENDS:${PN}-python = "python3-core"
