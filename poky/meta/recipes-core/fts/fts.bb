# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "POSIX file tree stream operations library"
HOMEPAGE = "https://sites.google.com/a/bostic.com/keithbostic"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5ffe358174aad383f1b69ce3b53da982"
SECTION = "libs"

SRCREV = "944333aed9dc24cfa76cc64bfe70c75d25652753"
PV = "1.2+git${SRCPV}"

SRC_URI = "git://github.com/voidlinux/musl-fts \
"
S = "${WORKDIR}/git"

inherit autotools pkgconfig
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"

