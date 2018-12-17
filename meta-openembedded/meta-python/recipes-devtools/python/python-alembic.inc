DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c5f1361296835ff94bda73db7dd312a6"

SRC_URI[md5sum] = "762ab9ba56d1c0b1bfbc7c9f7f849c92"
SRC_URI[sha256sum] = "52d73b1d750f1414fa90c25a08da47b87de1e4ad883935718a8f36396e19e78e"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
