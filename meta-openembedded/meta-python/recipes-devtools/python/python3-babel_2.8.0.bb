DESCRIPTION = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=942469df9305abb1c59e95f778310384"

SRC_URI[md5sum] = "6fad9772e75421969ddb41975483abdf"
SRC_URI[sha256sum] = "1aac2ae2d0d8ea368fa90906567f5c08463d98ade155c0c4bfedd6a0f7160e38"

PYPI_PACKAGE = "Babel"

inherit pypi setuptools3

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

BBCLASSEXTEND = "native nativesdk"
