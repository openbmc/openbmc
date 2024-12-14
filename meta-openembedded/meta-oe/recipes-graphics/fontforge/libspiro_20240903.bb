# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "LibSpiro is an adaptation of Spiro formula and functions into a sharable library"
HOMEPAGE = "https://github.com/fontforge/libspiro"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/fontforge/libspiro/releases/download/${PV}/libspiro-dist-${PV}.tar.gz"
SRC_URI[sha256sum] = "1412a21b943c6e1db834ee2d74145aad20b3f62b12152d475613b8241d9cde10"

UPSTREAM_CHECK_URI = "https://github.com/fontforge/libspiro/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+)"

inherit autotools

BBCLASSEXTEND = "native"
