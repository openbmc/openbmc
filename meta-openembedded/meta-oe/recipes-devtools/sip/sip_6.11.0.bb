# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A Python bindings generator for C/C++ libraries"

HOMEPAGE = "https://github.com/Python-SIP/sip"
LICENSE = "BSD-2-Clause"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://LICENSE;md5=236276327275fdb261636fb40b18d88d"

inherit pypi python_setuptools_build_meta python3native

PYPI_PACKAGE = "sip"
SRC_URI[sha256sum] = "237d24ead97a5ef2e8c06521dd94c38626e43702a2984c8a2843d7e67f07e799"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} = " \
    python3-core \
    python3-packaging \
    python3-logging \
    python3-tomllib \
    python3-setuptools \
"

BBCLASSEXTEND = "native"
