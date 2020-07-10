DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c19467890539ff718c00a019c9c7a7b2"

SRC_URI[md5sum] = "96e085d318297b1ad36ef2685f54287a"
SRC_URI[sha256sum] = "da2fb75f64792c1fc64c82313a00c728a7c301efe6a60b7a9fe35b16b4368ce7"

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
