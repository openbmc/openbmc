SUMMARY = "A Simple library for communicating with USB and Bluetooth HID devices"
AUTHOR = "Alan Ott"
HOMEPAGE = "http://www.signal11.us/oss/hidapi/"
SECTION = "libs"

LICENSE = "BSD-3-Clause | GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7c3949a631240cb6c31c50f3eb696077"

DEPENDS = "libusb udev"

inherit autotools pkgconfig

SRC_URI = "git://github.com/libusb/hidapi.git;protocol=https"
PV = "0.9.0"
SRCREV = "7da5cc91fc0d2dbe4df4f08cd31f6ca1a262418f"
S = "${WORKDIR}/git"
