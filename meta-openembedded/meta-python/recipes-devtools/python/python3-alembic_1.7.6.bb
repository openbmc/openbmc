DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f5a8522010db1a393833988dbe2c7f0b"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6c0c05e9768a896d804387e20b299880fe01bc56484246b0dffe8075d6d3d847"

PYPI_PACKAGE = "alembic"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
    ${PYTHON_PN}-misc \
"

BBCLASSEXTEND = "native nativesdk"
