DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a5befaf254582f10aadd62d83cd20322"

SRC_URI[md5sum] = "d1ebe49c7f71dfbd0daa8905df95668b"
SRC_URI[sha256sum] = "40b9a619aa5f25ea1e1508adcda88b33704ef28e02c9cfa6471e5c772ecf0829"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
