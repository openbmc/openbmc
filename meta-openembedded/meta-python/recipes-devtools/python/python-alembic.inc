DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=28aaf14a6592d14dbfb2f4abd597aa27"

SRC_URI[md5sum] = "3d7584a8fb0203ec82b90baa726d2bd9"
SRC_URI[sha256sum] = "3b0cb1948833e062f4048992fbc97ecfaaaac24aaa0d83a1202a99fb58af8c6d"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
