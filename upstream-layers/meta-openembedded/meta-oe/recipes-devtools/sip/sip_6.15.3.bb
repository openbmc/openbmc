# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A Python bindings generator for C/C++ libraries"

HOMEPAGE = "https://github.com/Python-SIP/sip"
LICENSE = "BSD-2-Clause"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://LICENSE;md5=236276327275fdb261636fb40b18d88d"

inherit pypi python_setuptools_build_meta python3native

PYPI_PACKAGE = "sip"
SRC_URI[sha256sum] = "bb2516983f9f716d321e5157c00d0de0c12422eba73b8f43a44610a0f6622438"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} = " \
    python3-core \
    python3-packaging \
    python3-logging \
    python3-tomllib \
    python3-setuptools \
"

BBCLASSEXTEND = "native"
