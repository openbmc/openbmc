DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3359ed561ac16aaa25b6c6eff84df595"

SRC_URI[sha256sum] = "76ff246881f528089bf19385131b966197bb494653990396d2ce138e2a447583"

PYPI_PACKAGE = "SQLAlchemy"
inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-profile \
"

BBCLASSEXTEND = "native nativesdk"
