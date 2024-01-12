DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c78b979ae6c20775a28a287d32092cbb"

SRC_URI[sha256sum] = "a2c69a7664fb2d54b8682dd774c3b54f67f84fa123cf84dda2a5f40dcaa04e08"

PYPI_PACKAGE = "SQLAlchemy"
inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-compression \
    python3-json \
    python3-logging \
    python3-netclient \
    python3-numbers \
    python3-pickle \
    python3-profile \
    python3-threading \
    python3-typing-extensions \
"

BBCLASSEXTEND = "native nativesdk"
