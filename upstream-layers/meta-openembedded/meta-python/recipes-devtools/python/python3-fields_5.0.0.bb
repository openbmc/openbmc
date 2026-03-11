# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Container class boilerplate killer."
HOMEPAGE = "https://github.com/ionelmc/python-fields"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e32c6705498713d9c904a9f565953a2c"

SRC_URI[sha256sum] = "31d4aa03d8d44e35df13c431de35136997f047a924a597d84f7bc209e1be5727"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"
