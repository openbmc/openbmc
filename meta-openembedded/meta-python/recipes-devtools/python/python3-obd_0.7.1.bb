DESCRIPTION = "A python module for handling realtime sensor data from OBD-II vehicle ports"HOMEPAGE = "https://github.com/brendan-w/python-OBD"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.md;md5=58ba896fa086c96ad23317cebfeab277"

SRC_URI[md5sum] = "305efcb6c650db7b9583532355ebeb7c"
SRC_URI[sha256sum] = "8b81ea5896157b6e861af12e173c10b001cb6cca6ebb04db2c01d326812ad77b"

inherit setuptools3 pypi

RDEPENDS_${PN} += "${PYTHON_PN}-pyserial ${PYTHON_PN}-pint ${PYTHON_PN}-setuptools ${PYTHON_PN}-packaging"
