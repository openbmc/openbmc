SUMMARY = "Kernel evdev device emulation"
DESCRIPTION = "The evemu library and tools are used to describe devices, record data, create devices and replay data from kernel evdev devices."
HOMEPAGE = "https://www.freedesktop.org/wiki/Evemu"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEPENDS = "libevdev"

SRCREV = "86a5627dbeac8d9d9bc34326a758d6a477e876e4"
SRC_URI = "git://git@gitlab.freedesktop.org/libevdev/evemu.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
PV = "2.7.0+git"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-python"
FILES:${PN}-python = "${libdir}/python*/site-packages/*"
RDEPENDS:${PN}-python = "python3"

