# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=fdbc58a6da11a9f68aa73c453818decc"

SRC_URI = "git://github.com/c-ares/c-ares.git;branch=main;protocol=https"
SRCREV = "36466bb240661fa8fdc2ffa47d229d92ac762286"

UPSTREAM_CHECK_GITTAGREGEX = "cares-(?P<pver>\d+_(\d_?)+)"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-utils"

FILES:${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
