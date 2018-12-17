DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "udev libusb1 libplist"

inherit autotools pkgconfig gitpkgv

PKGV = "${GITPKGVTAG}"

SRCREV = "78df9be5fc8222ed53846cb553de9b5d24c85c6c"
SRC_URI = "git://github.com/libimobiledevice/libusbmuxd;protocol=https"

S = "${WORKDIR}/git"

FILES_${PN} += "${base_libdir}/udev/rules.d/"
