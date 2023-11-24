DESCRIPTION = "i18n and l10n support for Flask based on babel and pytz"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51917f3e8e858f5ae295a7d0e2eb3cc9"

SRC_URI[sha256sum] = "dbeab4027a3f4a87678a11686496e98e1492eb793cbdd77ab50f4e9a2602a593"

PYPI_PACKAGE = "flask_babel"

inherit pypi python_poetry_core

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-speaklater \
    ${PYTHON_PN}-babel \
    ${PYTHON_PN}-flask \
    "
