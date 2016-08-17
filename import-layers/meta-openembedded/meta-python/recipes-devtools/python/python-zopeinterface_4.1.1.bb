SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e54fd776274c1b7423ec128974bd9d46"

SRC_URI[md5sum] = "edcd5f719c5eb2e18894c4d06e29b6c6"
SRC_URI[sha256sum] = "91cba7b7cd7cb82f6f4e023fe77f94dc3df4ae5287fd55def2148dc232d0c7da"

PYPI_PACKAGE = "zope.interface"
inherit pypi setuptools

RPROVIDES_${PN} += "zope-interfaces"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*.egg/*/*/.debug"
FILES_${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.c"
FILES_${PN}-doc += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.txt"
FILES_${PN}-tests = " \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/tests \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/common/tests \
"
