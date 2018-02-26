DESCRIPTION = "A collection of tools for internationalizing Python applications"
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6eeaabc92cfc2d03f53e789324d7292"

SRC_URI[md5sum] = "60228b3ce93a203357158b909afe8ae1"
SRC_URI[sha256sum] = "6007daf714d0cd5524bbe436e2d42b3c20e68da66289559341e48d2cd6d25811"

PYPI_PACKAGE = "Babel"

inherit pypi

CLEANBROKEN = "1"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-pytz \
    ${PYTHON_PN}-threading \
"
