DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a5befaf254582f10aadd62d83cd20322"

SRC_URI[md5sum] = "9969c707786f41f1b95c09c54f8970e9"
SRC_URI[sha256sum] = "828dcaa922155a2b7166c4f36ec45268944e4055c86499bd14319b4c8c0094b7"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
