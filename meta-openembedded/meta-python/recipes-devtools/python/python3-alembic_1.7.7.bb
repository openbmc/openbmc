DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f5a8522010db1a393833988dbe2c7f0b"

inherit pypi setuptools3

SRC_URI[sha256sum] = "4961248173ead7ce8a21efb3de378f13b8398e6630fab0eb258dc74a8af24c58"

PYPI_PACKAGE = "alembic"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
    ${PYTHON_PN}-misc \
"

BBCLASSEXTEND = "native nativesdk"
