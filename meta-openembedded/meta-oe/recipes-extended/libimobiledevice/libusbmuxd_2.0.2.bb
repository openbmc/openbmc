DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "udev libusb1 libplist"

inherit autotools pkgconfig gitpkgv

PKGV = "${GITPKGVTAG}"

SRCREV = "ce98c346b7c1dc2a21faea4fd3f32c88e27ca2af"
SRC_URI = "git://github.com/libimobiledevice/libusbmuxd;protocol=https;branch=master"

S = "${WORKDIR}/git"

FILES:${PN} += "${base_libdir}/udev/rules.d/"
