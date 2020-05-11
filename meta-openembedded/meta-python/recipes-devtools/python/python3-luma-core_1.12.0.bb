SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=eda804060ba2312e41fe96b6fa334fd7"

inherit pypi setuptools3

SRC_URI[md5sum] = "4378edb99cd12540b4e4a588969567ee"
SRC_URI[sha256sum] = "864a427de78bcc16758f4f4402a9e61f31cc8857bfae9aba8041159aaec3a491"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.core"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-pillow \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-smbus2 \
"
