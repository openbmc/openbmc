DESCRIPTION = "A python module for handling realtime sensor data from OBD-II vehicle ports"HOMEPAGE = "https://github.com/brendan-w/python-OBD"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://README.md;md5=176d0f9ad45c10895296260d426fa862"

SRC_URI[sha256sum] = "20d38c9ded3daad1e8affab3ff367a70788d4f29ac77ab7aacddc6a6d2a43d61"

inherit setuptools3 pypi

RDEPENDS:${PN} += "${PYTHON_PN}-pyserial ${PYTHON_PN}-pint ${PYTHON_PN}-setuptools ${PYTHON_PN}-packaging"
