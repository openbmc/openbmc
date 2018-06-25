DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=452f4b8adb0feba42e5be5f1fbfbf538"
RDEPENDS_${PN} += "python-numbers"

SRCNAME = "SQLAlchemy"
SRC_URI = "https://pypi.python.org/packages/da/04/8048a5075d6e29235bbd6f1ea092a38dbe2630c670e73d4aa923a4e5521c/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "50685d97dca4b91945ae6309d03ab8c9"
SRC_URI[sha256sum] = "68fb40049690e567ebda7b270176f5abf0d53d9fbd515fec4e43326f601119b6"

S = "${WORKDIR}/${SRCNAME}-${PV}"

DEFAULT_PREFERENCE = "-1"

inherit setuptools
