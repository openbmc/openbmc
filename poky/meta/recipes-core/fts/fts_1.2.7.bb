# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Implementation of ftsfor musl libc packages"
HOMEPAGE = "https://github.com/pullmoll/musl-fts"
DESCRIPTION = "The musl-fts package implements the fts(3) functions fts_open, fts_read, fts_children, fts_set and fts_close, which are missing in musl libc."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5ffe358174aad383f1b69ce3b53da982"
SECTION = "libs"

SRCREV = "0bde52df588e8969879a2cae51c3a4774ec62472"

SRC_URI = "git://github.com/pullmoll/musl-fts.git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"

