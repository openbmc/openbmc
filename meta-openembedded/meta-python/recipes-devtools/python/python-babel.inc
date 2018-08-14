DESCRIPTION = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6eeaabc92cfc2d03f53e789324d7292"

SRC_URI[md5sum] = "152a6b17fe4110b95675aceb9af9fab2"
SRC_URI[sha256sum] = "8ce4cb6fdd4393edd323227cba3a077bceb2a6ce5201c902c65e730046f41f14"

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
