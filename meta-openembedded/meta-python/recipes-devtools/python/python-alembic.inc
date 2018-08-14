DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c5f1361296835ff94bda73db7dd312a6"

SRC_URI[md5sum] = "5cfef58641c9a94d4a5d547e951a7dda"
SRC_URI[sha256sum] = "13b8611788acf0d7b617775db5c2ae26554a6d4263c590ef628d448fd05aef56"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
