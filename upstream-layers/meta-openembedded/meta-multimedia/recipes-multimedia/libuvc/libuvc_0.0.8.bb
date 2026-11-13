SUMMARY = "library for USB video devices built atop libusb"
HOMEPAGE = "https://github.com/libuvc/libuvc.git"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2f1963e0bb88c93463af750daf9ba0c2"

DEPENDS = "libusb"

SRC_URI = "git://github.com/libuvc/libuvc.git;branch=master;protocol=https;tag=v${PV}"

SRCREV = "4e9fc773914377ec0bcf2f31621f56da5a0fa09f"

inherit cmake pkgconfig

PACKAGECONFIG ?= "jpeg"
PACKAGECONFIG[jpeg] = "-DDISABLE_JPEG=OFF,-DDISABLE_JPEG=ON,jpeg"
