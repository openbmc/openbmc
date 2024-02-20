DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "udev libusb1 libplist libimobiledevice-glue"

inherit autotools pkgconfig gitpkgv

PKGV = "${GITPKGVTAG}"
PV = "2.0.2+git"

SRCREV = "36ffb7ab6e2a7e33bd1b56398a88895b7b8c615a"
SRC_URI = "git://github.com/libimobiledevice/libusbmuxd;protocol=https;branch=master"

S = "${WORKDIR}/git"

FILES:${PN} += "${base_libdir}/udev/rules.d/"
