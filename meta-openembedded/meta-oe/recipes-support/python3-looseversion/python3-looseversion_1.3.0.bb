# Copyright (C) 2024 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Version numbering for anarchists and software realists"
HOMEPAGE = "https://github.com/effigies/looseversion"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d5605fc335ce1bab614032468d0a1e00"

DEPENDS = "python3-hatchling-native"
SRC_URI[sha256sum] = "ebde65f3f6bb9531a81016c6fef3eb95a61181adc47b7f949e9c0ea47911669e"

inherit pypi python_hatchling

PYPI_PACKAGE = "looseversion"
