# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A Python bindings generator for C/C++ libraries"

HOMEPAGE = "https://www.riverbankcomputing.com/software/sip/"
LICENSE = "GPL-2.0-or-later"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed1d69a33480ebf4ff8a7a760826d84e"

inherit pypi python_setuptools_build_meta python3native

PYPI_PACKAGE = "sip"
SRC_URI[sha256sum] = "7904be5190d7879952563b78a3af0e58fa27d9525af7f53f93eac7a83b433e7b"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} = " \
    python3-core \
    python3-packaging \
    python3-logging \
    python3-tomllib \
    python3-setuptools \
"

BBCLASSEXTEND = "native"
