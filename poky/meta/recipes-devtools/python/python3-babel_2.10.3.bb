DESCRIPTION = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1b3f4650099e6d6a73e5a7fc8774ff18"

SRC_URI[sha256sum] = "7614553711ee97490f732126dc077f8d0ae084ebc6a96e23db1482afabdb2c51"

PYPI_PACKAGE = "Babel"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
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
