SUMMARY = "A Simple library for communicating with USB and Bluetooth HID devices"
AUTHOR = "Alan Ott"
HOMEPAGE = "http://www.signal11.us/oss/hidapi/"
SECTION = "libs"

LICENSE = "BSD-3-Clause | GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7c3949a631240cb6c31c50f3eb696077"

DEPENDS = "libusb udev"
RDEPENDS:${PN}:append:libc-glibc = " glibc-gconv-utf-16"

inherit autotools pkgconfig

SRC_URI = "git://github.com/libusb/hidapi.git;protocol=https;branch=master"
PV = "0.12.0"
SRCREV = "76108294092c023a4ece99eb3219559cea0d5066"
S = "${WORKDIR}/git"
