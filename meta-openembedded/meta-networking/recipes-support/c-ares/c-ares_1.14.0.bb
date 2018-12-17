# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=fb997454c8d62aa6a47f07a8cd48b006"

PV = "1.14.0+gitr${SRCPV}"

SRC_URI = "\
    git://github.com/c-ares/c-ares.git \
    file://cmake-install-libcares.pc.patch \
"
SRCREV = "17dc1b3102e0dfc3e7e31369989013154ee17893"

UPSTREAM_CHECK_GITTAGREGEX = "cares-(?P<pver>\d+_(\d_?)+)"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-utils"

FILES_${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
