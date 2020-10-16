SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=eda804060ba2312e41fe96b6fa334fd7"

inherit pypi setuptools3

SRC_URI[md5sum] = "dea76a83c7130f59b7e807c3b2f1b3b5"
SRC_URI[sha256sum] = "b4a9f934f4dbf9b1d5c74259f871b4187aaa754b139964153f7702badaaccffd"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.core"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-pillow \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-smbus2 \
"
