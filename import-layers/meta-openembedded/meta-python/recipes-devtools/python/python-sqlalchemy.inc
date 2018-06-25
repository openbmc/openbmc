DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1c412c9d8d1ce018f7a9374407c22bb"

PYPI_PACKAGE = "SQLAlchemy"
inherit pypi

SRC_URI[md5sum] = "03db1c8f8ae911977c0bf3950a4c9c21"
SRC_URI[sha256sum] = "6b55ec379c11ad696f9371ce8e0de93c7f21134323f56a56a1b0d8fbef17013c"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
"
