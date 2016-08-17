DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf755cb27ad4331d45dbb4db5172fd33"
RDEPENDS_${PN} += "python-numbers"

SRCNAME = "SQLAlchemy"
SRC_URI = "https://pypi.python.org/packages/source/S/${SRCNAME}/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "7cfd005be63945c96a78c67764ac3a85"
SRC_URI[sha256sum] = "950c79c0abf9e9f99c43c627c51d40d14a946810a90c35e7cd827bfd0bffe46f"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
