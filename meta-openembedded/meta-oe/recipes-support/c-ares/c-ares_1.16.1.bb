# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=fb997454c8d62aa6a47f07a8cd48b006"

PV = "1.16.1+gitr${SRCPV}"

SRC_URI = "\
    git://github.com/c-ares/c-ares.git;branch=main;protocol=https \
    file://cmake-install-libcares.pc.patch \
    file://0001-fix-configure-error-mv-libcares.pc.cmakein-to-libcar.patch \
    file://ares_expand_name-should-escape-more-characters.patch \
    file://ares_expand_name-fix-formatting-and-handling-of-root.patch \
"
SRCREV = "74a1426ba60e2cd7977e53a22ef839c87415066e"

UPSTREAM_CHECK_GITTAGREGEX = "cares-(?P<pver>\d+_(\d_?)+)"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-utils"

FILES_${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
