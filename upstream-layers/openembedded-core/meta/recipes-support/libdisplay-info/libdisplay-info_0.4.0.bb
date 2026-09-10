SUMMARY = "EDID and DisplayID library"
DESCRIPTION = "The goal of this library is to provide a set of high-level, \
easy-to-use, and opinionated functions, as well as low-level functions for \
accessing detailed information, prioritizing simplicity and correctness over \
performance and resource usage, while being well-tested and fuzzed."
HOMEPAGE = "https://gitlab.freedesktop.org/emersion/libdisplay-info"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4426409957080ee0352128354cea2de"
DEPENDS = "hwdata-native"

SRC_URI = "git://gitlab.freedesktop.org/emersion/libdisplay-info.git;branch=main;protocol=https;tag=${PV}"
SRCREV = "c67a3e9bedb05ab61c7443704a1a107e76254595"

inherit meson pkgconfig lib_package
