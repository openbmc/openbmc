DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "udev libusb1 libplist libimobiledevice-glue"

inherit autotools pkgconfig gitpkgv

PKGV = "${GITPKGVTAG}"

SRCREV = "adf9c22b9010490e4b55eaeb14731991db1c172c"
SRC_URI = "git://github.com/libimobiledevice/libusbmuxd;protocol=https;branch=master"


FILES:${PN} += "${base_libdir}/udev/rules.d/"
