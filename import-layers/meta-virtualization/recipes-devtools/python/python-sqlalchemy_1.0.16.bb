DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7dba1721bc8ce05d421f7279cb87971"
RDEPENDS_${PN} += "python-numbers"

SRCNAME = "SQLAlchemy"
SRC_URI = "https://pypi.io/packages/source/S/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "42c81726e7e145c206dac46964b4a167"
SRC_URI[sha256sum] = "e2dfdaa0983931ac1b0522dd637f08a52cf3081746513ac79c50843277ebe463"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
