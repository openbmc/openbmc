SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e54fd776274c1b7423ec128974bd9d46"

SRC_URI[md5sum] = "a3b24f9d079bae5e13dd7a88aa512112"
SRC_URI[sha256sum] = "1b3d0dcabc7c90b470e59e38a9acaa361be43b3a6ea644c0063951964717f0e5"

PYPI_PACKAGE = "zope.interface"

PACKAGES =. "${PN}-test "

RPROVIDES_${PN} += "zope-interfaces"

RDEPENDS_${PN}_class-target += "${PYTHON_PN}-datetime"

FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/*.egg/*/*/.debug"
FILES_${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.c"
FILES_${PN}-doc += "${PYTHON_SITEPACKAGES_DIR}/zope/interface/*.txt"
FILES_${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/tests \
        ${PYTHON_SITEPACKAGES_DIR}/zope/interface/common/tests \
"
