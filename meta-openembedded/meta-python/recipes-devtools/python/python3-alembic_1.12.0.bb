DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3023b042cb6002cb398344b51c67093"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8e7645c32e4f200675e69f0745415335eb59a3663f5feb487abfa0b30c45888b"

PYPI_PACKAGE = "alembic"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
    ${PYTHON_PN}-misc \
"

BBCLASSEXTEND = "native nativesdk"
