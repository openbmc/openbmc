DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c19467890539ff718c00a019c9c7a7b2"

SRC_URI[md5sum] = "40200b570274446a05959abd3fa81778"
SRC_URI[sha256sum] = "d2f25c7f410338d31666d7ddedfa67570900e248b940d186b48461bd4e5569a1"

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
