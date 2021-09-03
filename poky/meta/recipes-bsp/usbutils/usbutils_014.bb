SUMMARY = "Host side USB console utilities"
DESCRIPTION = "Contains the lsusb utility for inspecting the devices connected to the USB bus."
HOMEPAGE = "http://www.linux-usb.org"
SECTION = "base"

LICENSE = "GPLv2+ & (GPLv2 | GPLv3)"
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

# The binaries are mostly GPLv2+ apart from lsusb.py which is GPLv2 or v3.
LICENSE:${PN} = "GPLv2+"
LICENSE:${PN}-python = "GPLv2 | GPLv3"

RRECOMMENDS:${PN} = "udev-hwdb"

PACKAGE_BEFORE_PN =+ "${PN}-python"
FILES:${PN}-python += "${bindir}/lsusb.py"
RDEPENDS:${PN}-python = "python3-core"
