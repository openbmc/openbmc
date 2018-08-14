SUMMARY = "Multi-platform library to interface with USB and Bluetooth HID-Class devices"
AUTHOR = "Alan Ott"
HOMEPAGE = "http://www.signal11.us/oss/hidapi/"
SECTION = "libs"
LICENSE = "BSD-3-Clause | GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7c3949a631240cb6c31c50f3eb696077"
DEPENDS = "libusb udev"
PV = "0.7.99+0.8.0-rc1+git${SRCPV}"

SRCREV = "d17db57b9d4354752e0af42f5f33007a42ef2906"
SRC_URI = "git://github.com/signal11/hidapi.git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
