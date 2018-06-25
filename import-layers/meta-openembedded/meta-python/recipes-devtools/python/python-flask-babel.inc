DESCRIPTION = "i18n and l10n support for Flask based on babel and pytz"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51917f3e8e858f5ae295a7d0e2eb3cc9"

SRC_URI[md5sum] = "07803453743f139309021572a799c6b3"
SRC_URI[sha256sum] = "c0d75710bd4b0fe866f9f2347de6e19208712f9cec006436b4c1c15d4cb0c939"

PYPI_PACKAGE = "Flask-Babel"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-speaklater \
    ${PYTHON_PN}-babel \
    ${PYTHON_PN}-flask \
    "
