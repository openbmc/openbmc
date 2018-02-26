# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f4b026880834eb01c035c5e5cb47ccac"
SRCREV = "3be1924221e1326df520f8498d704a5c4c8d0cce"
PV = "1.13.0+gitr${SRCPV}"

SRC_URI = "\
    git://github.com/c-ares/c-ares.git \
    file://cmake-install-libcares.pc.patch \
"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-utils"

FILES_${PN}-dev += "${libdir}/cmake"
FILES_${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native"
