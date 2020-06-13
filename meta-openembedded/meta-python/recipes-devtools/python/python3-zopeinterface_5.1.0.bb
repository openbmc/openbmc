SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e54fd776274c1b7423ec128974bd9d46"

PYPI_PACKAGE = "zope.interface"

inherit pypi setuptools3
SRC_URI[sha256sum] = "40e4c42bd27ed3c11b2c983fecfb03356fae1209de10686d03c02c8696a1d90e"

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
