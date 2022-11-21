SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e54fd776274c1b7423ec128974bd9d46"

PYPI_PACKAGE = "zope.interface"

inherit pypi setuptools3
SRC_URI[sha256sum] = "6d678475fdeb11394dc9aaa5c564213a1567cc663082e0ee85d52f78d1fbaab2"

PACKAGES =. "${PN}-test "

RPROVIDES:${PN} += "zope-interfaces"

RDEPENDS:${PN}:class-target += "${PYTHON_PN}-datetime"
RDEPENDS:${PN}-test += "python3-unittest python3-doctest"

FILES:${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*.egg/*/*/.debug"
FILES:${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.c"
FILES:${PN}-doc += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.txt"
FILES:${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/tests \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/common/tests \
"
