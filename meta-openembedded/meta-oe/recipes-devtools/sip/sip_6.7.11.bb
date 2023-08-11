# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A Python bindings generator for C/C++ libraries"

HOMEPAGE = "https://www.riverbankcomputing.com/software/sip/"
LICENSE = "GPL-2.0-or-later"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://LICENSE-GPL2;md5=e91355d8a6f8bd8f7c699d62863c7303"

inherit pypi setuptools3 python3native

PYPI_PACKAGE = "sip"
SRC_URI[sha256sum] = "f0dc3287a0b172e5664931c87847750d47e4fdcda4fe362b514af8edd655b469"

BBCLASSEXTEND = "native"
