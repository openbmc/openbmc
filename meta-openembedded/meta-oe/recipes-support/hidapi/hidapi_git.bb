SUMMARY = "A Simple library for communicating with USB and Bluetooth HID devices"
AUTHOR = "Alan Ott"
HOMEPAGE = "http://www.signal11.us/oss/hidapi/"
SECTION = "libs"

LICENSE = "BSD-3-Clause | GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7c3949a631240cb6c31c50f3eb696077"

DEPENDS = "libusb udev"

inherit autotools pkgconfig

SRC_URI = "git://github.com/libusb/hidapi.git;protocol=https \
           file://0001-configure.ac-remove-duplicate-AC_CONFIG_MACRO_DIR-22.patch \
"
PV = "0.10.1"
SRCREV = "f6d0073fcddbdda24549199445e844971d3c9cef"
S = "${WORKDIR}/git"
