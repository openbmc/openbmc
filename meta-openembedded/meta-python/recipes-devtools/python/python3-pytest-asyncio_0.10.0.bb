DESCRIPTION = "pytest-asyncio is an Apache2 licensed library, written in Python, for testing asyncio code with pytest"
HOMEPAGE = "https://github.com/pytest-dev/pytest-asyncio"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=ae62268d207c73b615fbefddaf91a881"

SRC_URI[md5sum] = "247a7ec32f24a185341327c42a0f85bf"
SRC_URI[sha256sum] = "9fac5100fd716cbecf6ef89233e8590a4ad61d729d1732e0a96b84182df1daaf"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-pytest-native"

BBCLASSEXTEND = "native"
