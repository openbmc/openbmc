DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baffc5e5f4804c92fc9be155fed70d41"
RDEPENDS_${PN} += "python-numbers"

SRCNAME = "SQLAlchemy"
SRC_URI = "${SOURCEFORGE_MIRROR}/sqlalchemy/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "c4852d586d95a59fbc9358f4467875d5"
SRC_URI[sha256sum] = "f7a305ad122144f364ce09a2d9ed5159d5f46ec43650653593e7dfa05d3294a1"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
