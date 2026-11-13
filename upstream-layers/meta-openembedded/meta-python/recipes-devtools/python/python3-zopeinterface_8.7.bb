SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78ccb3640dc841e1baecb3e27a6966b2"

PYPI_PACKAGE = "zope-interface"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"
SRC_URI[sha256sum] = "0b47b62e8d0d99b24bcdd32f4f2120425e5019c3bee2ad69a0e1d75737487a96"
PACKAGES =. "${PN}-test "

RPROVIDES:${PN} += "zope-interfaces"

RDEPENDS:${PN}:append:class-target = " python3-datetime"
RDEPENDS:${PN}-test += "python3-unittest python3-doctest"

FILES:${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*.egg/*/*/.debug"
FILES:${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.c"
FILES:${PN}-doc += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.txt"
FILES:${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/tests \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/common/tests \
"
