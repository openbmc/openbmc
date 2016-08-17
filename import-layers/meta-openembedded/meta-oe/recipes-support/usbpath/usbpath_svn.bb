SUMMARY = "Convert the physical locations of a USB device to/from its number"
AUTHOR = "Werner Almesberger <werner@openmoko.org>"
SECTION = "console/utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://usbpath.c;endline=20;md5=0aa8c7d2af9110c78a99fbf9a504dc3f"
DEPENDS = "virtual/libusb0"
DEPENDS_class-native = "virtual/libusb0-native"

BBCLASSEXTEND = "native"

SRCREV = "3172"
PV = "0.0+svnr${SRCPV}"

SRC_URI = "svn://svn.openmoko.org/trunk/src/host;module=usbpath;protocol=http \
           file://configure.patch"

S = "${WORKDIR}/usbpath"

inherit autotools pkgconfig

RDEPENDS_${PN} += "perl"
