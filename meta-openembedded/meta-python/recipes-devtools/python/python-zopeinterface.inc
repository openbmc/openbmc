SUMMARY = "Interface definitions for Zope products"
LICENSE = "ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e54fd776274c1b7423ec128974bd9d46"

SRC_URI[md5sum] = "7b669cd692d817772c61d2e3ad0f1e71"
SRC_URI[sha256sum] = "57c38470d9f57e37afb460c399eb254e7193ac7fb8042bd09bdc001981a9c74c"

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
