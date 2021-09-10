# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "``aspectlib`` is an aspect-oriented programming, monkey-patch and decorators library. It is useful when changing"
HOMEPAGE = "https://github.com/ionelmc/python-aspectlib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d58b3f20fd10347a9458b8a03793b62e"

SRC_URI[sha256sum] = "d275ec82c4c2712e564bb760e4accff8f061f648e38774feabeb8b241cf3a4aa"

inherit ptest pypi setuptools3

SRC_URI += "file://run-ptest \
            file://0001-Remove-tornado-6-test-constraint.-Ref-15.patch \
           "

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += "\
    python3-tornado \
    python3-process-tests \
"

RDEPENDS:${PN} += "python3-core python3-fields"

BBCLASSEXTEND = "native nativesdk"
