DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c19467890539ff718c00a019c9c7a7b2"

SRC_URI[md5sum] = "478214152b9293bf5652815d7312c890"
SRC_URI[sha256sum] = "156a27548ba4e1fed944ff9fcdc150633e61d350d673ae7baaf6c25c04ac1f71"

PYPI_PACKAGE = "SQLAlchemy"
inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
"
