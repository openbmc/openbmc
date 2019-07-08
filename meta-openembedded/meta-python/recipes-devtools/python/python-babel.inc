DESCRIPTION = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eebdc057dc12abcc2d6c4453628bfcd4"

SRC_URI[md5sum] = "c384ac03026e8fe6f9b90f55201f1bff"
SRC_URI[sha256sum] = "8cba50f48c529ca3fa18cf81fa9403be176d374ac4d60738b839122dfaaa3d23"

PYPI_PACKAGE = "Babel"

inherit pypi

CLEANBROKEN = "1"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-difflib \
    ${PYTHON_PN}-distutils \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pytz \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-threading \
"
