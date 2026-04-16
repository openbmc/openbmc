SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78ccb3640dc841e1baecb3e27a6966b2"

PYPI_PACKAGE = "zope_interface"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "e1a9de7d0b5b5c249a73b91aebf4598ce05e334303af6aa94865893283e9ff10"
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
