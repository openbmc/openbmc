DESCRIPTION = "i18n and l10n support for Flask based on babel and pytz"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51917f3e8e858f5ae295a7d0e2eb3cc9"

SRC_URI[md5sum] = "50d5e92d96ef58787bf85b5a1b0a5567"
SRC_URI[sha256sum] = "f9faf45cdb2e1a32ea2ec14403587d4295108f35017a7821a2b1acb8cfd9257d"

PYPI_PACKAGE = "Flask-Babel"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-speaklater \
    ${PYTHON_PN}-babel \
    ${PYTHON_PN}-flask \
    "
