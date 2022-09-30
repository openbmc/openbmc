# Copyright (C) 2020 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Simple utility for swapping an fbtft-based device as the console."
HOMEPAGE = "https://gitlab.com/pibox/con2fbmap"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "console/utils"
DEPENDS = ""

SRCREV = "61ed2f28b294b1ebeb767df8cb5fcd391709c8e2"
SRC_URI = "git://gitlab.com/pibox/con2fbmap.git;protocol=https;branch=master \
           file://0001-con2fbmap-Add-missing-include-on-string.h.patch \
           "

S = "${WORKDIR}/git"

inherit autotools

