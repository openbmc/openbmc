SUMMARY = "Host side USB console utilities"
DESCRIPTION = "Contains the lsusb utility for inspecting the devices connected to the USB bus."
HOMEPAGE = "http://www.linux-usb.org"
SECTION = "base"

LICENSE = "GPLv2+ & (GPLv2 | GPLv3)"
# License files went missing in 010, when 011 is released add LICENSES/* back
LIC_FILES_CHKSUM = "file://lsusb.c;endline=1;md5=7d4861d978ff5ba7cb2b319ed1d4afe3 \
                    file://lsusb.py.in;beginline=2;endline=2;md5=194d6a0226bf90f4f683e8968878b6cd"

DEPENDS = "libusb1 virtual/libiconv udev"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/usb/usbutils/usbutils-${PV}.tar.gz \
          "
SRC_URI[md5sum] = "7484445cbcf04b3eacac892fe58f8d9f"
SRC_URI[sha256sum] = "ae2e10aad530d95839b6f4d46cd41715eae6f0f1789310d793e9be21b3e7ae20"

inherit autotools pkgconfig features_check update-alternatives

ALTERNATIVE_${PN} = "lsusb"
ALTERNATIVE_PRIORITY = "100"

# The binaries are mostly GPLv2+ apart from lsusb.py which is GPLv2 or v3.
LICENSE_${PN} = "GPLv2+"
LICENSE_${PN}-python = "GPLv2 | GPLv3"

RRECOMMENDS_${PN} = "udev-hwdb"

PACKAGE_BEFORE_PN =+ "${PN}-python"
FILES_${PN}-python += "${bindir}/lsusb.py"
RDEPENDS_${PN}-python = "python3-core"
