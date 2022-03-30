DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f4001d1ca15b69d096fa1b4fd1bdce79"

SRC_URI[sha256sum] = "6fdd2dc5931daab778c2b65b03df6ae68376e028a3098eb624d0909d999885bc"

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
