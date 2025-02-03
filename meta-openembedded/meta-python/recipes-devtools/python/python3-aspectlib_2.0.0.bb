# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "An aspect-oriented programming, monkey-patch and decorators library."
DESCRIPTION = " It is useful when changing behavior in existing code is desired. \
It includes tools for debugging and testing: simple mock/record and a complete capture/replay framework."
HOMEPAGE = "https://github.com/ionelmc/python-aspectlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=80721ace117fd1f814049ecb81c6be76"

SRC_URI[sha256sum] = "a4b461b9da0b531aebcb93efcde3de808a72c60226dd8d902c467d13faf7ce92"

inherit ptest-python-pytest pypi setuptools3

RDEPENDS:${PN}-ptest += "\
    python3-process-tests \
    python3-tornado \
"

RDEPENDS:${PN} += " \
    python3-fields \
    python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
