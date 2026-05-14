SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78ccb3640dc841e1baecb3e27a6966b2"

PYPI_PACKAGE = "zope_interface"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "9dbee7925a23aa6349738892c911019d4095a96cff487b743482073ecbc174a8"
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
