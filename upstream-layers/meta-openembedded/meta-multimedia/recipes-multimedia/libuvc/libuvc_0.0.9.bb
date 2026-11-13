SUMMARY = "library for USB video devices built atop libusb"
HOMEPAGE = "https://github.com/libuvc/libuvc.git"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2f1963e0bb88c93463af750daf9ba0c2"

DEPENDS = "libusb"

SRC_URI = "git://github.com/libuvc/libuvc.git;branch=master;protocol=https;tag=v${PV}"

SRCREV = "448d78b9aa4f54172c1ca5b5d1b4a6b9b93f7957"

inherit cmake pkgconfig

PACKAGECONFIG ?= "jpeg"
PACKAGECONFIG[jpeg] = "-DDISABLE_JPEG=OFF,-DDISABLE_JPEG=ON,jpeg"
