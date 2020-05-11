# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=fb997454c8d62aa6a47f07a8cd48b006"

PV = "1.16.0+gitr${SRCPV}"

SRC_URI = "\
    git://github.com/c-ares/c-ares.git \
    file://cmake-install-libcares.pc.patch \
    file://0001-fix-configure-error-mv-libcares.pc.cmakein-to-libcar.patch \
"
SRCREV = "077a587dccbe2f0d8a1987fbd3525333705c2249"

UPSTREAM_CHECK_GITTAGREGEX = "cares-(?P<pver>\d+_(\d_?)+)"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-utils"

FILES_${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
