DESCRIPTION = "i18n and l10n support for Flask based on babel and pytz"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51917f3e8e858f5ae295a7d0e2eb3cc9"

SRC_URI[md5sum] = "fcf2f360ff279d3133e40974804efd72"
SRC_URI[sha256sum] = "316ad183e42003f3922957fa643d0a1e8e34a0f0301a88c3a8f605bc37ba5c86"

PYPI_PACKAGE = "Flask-Babel"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-speaklater \
    ${PYTHON_PN}-babel \
    ${PYTHON_PN}-flask \
    "
