SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "http://rhodesmill.org/pyephem/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c930b395b435b00bb13ec83b0c99f40"

SRC_URI[sha256sum] = "dba9e05c78ce910ae75a06351a5592479191a8dc570ac0cd6d18a77e98138873"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
    "
