# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "LibSpiro is an adaptation of Spiro formula and functions into a sharable library"
HOMEPAGE = "https://github.com/fontforge/libspiro"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/fontforge/libspiro/releases/download/${PV}/libspiro-dist-${PV}.tar.gz"
SRC_URI[sha256sum] = "5984fb5af3e4e1f927f3a74850b705a711fb86284802a5e6170b09786440e8be"

inherit autotools

BBCLASSEXTEND = "native"
