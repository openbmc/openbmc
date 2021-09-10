# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Tools for testing processes."
HOMEPAGE = "https://github.com/ionelmc/python-process-tests"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37031056aff37e4b3310872a9a3d0b1e"

SRC_URI[sha256sum] = "a3747ad947bdfc93e5c986bdb17a6d718f3f26e8577a0807a00962f29e26deba"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"
