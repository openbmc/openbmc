SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e54fd776274c1b7423ec128974bd9d46"

PYPI_PACKAGE = "zope.interface"

inherit pypi setuptools3
SRC_URI[sha256sum] = "5dba5f530fec3f0988d83b78cc591b58c0b6eb8431a85edd1569a0539a8a5a0e"

PACKAGES =. "${PN}-test "

RPROVIDES_${PN} += "zope-interfaces"

RDEPENDS_${PN}_class-target += "${PYTHON_PN}-datetime"
RDEPENDS_${PN}-test += "python3-unittest python3-doctest"

FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*.egg/*/*/.debug"
FILES_${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.c"
FILES_${PN}-doc += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.txt"
FILES_${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/tests \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/common/tests \
"
