DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bd64aba1b968c2bfbc2b525a181ce85c"

inherit pypi setuptools3

SRC_URI[md5sum] = "1d67bdbd3abd33f0319afcd29bc59686"
SRC_URI[sha256sum] = "035ab00497217628bf5d0be82d664d8713ab13d37b630084da8e1f98facf4dbf"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
