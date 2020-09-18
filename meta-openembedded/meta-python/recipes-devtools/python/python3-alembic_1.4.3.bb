DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bd64aba1b968c2bfbc2b525a181ce85c"

inherit pypi setuptools3

SRC_URI[md5sum] = "6051576d8e25e3d4a9fd818d79531bad"
SRC_URI[sha256sum] = "5334f32314fb2a56d86b4c4dd1ae34b08c03cae4cb888bc699942104d66bc245"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
