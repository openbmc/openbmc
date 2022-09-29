# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "LibSpiro is an adaptation of Spiro formula and functions into a sharable library"
HOMEPAGE = "https://github.com/fontforge/libspiro"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/fontforge/libspiro/releases/download/20200505/libspiro-dist-20200505.tar.gz"
SRC_URI[sha256sum] = "06c69a1e8dcbcabcf009fd96fd90b1a244d0257246e376c2c4d57c4ea4af0e49"

inherit autotools

BBCLASSEXTEND = "native"
