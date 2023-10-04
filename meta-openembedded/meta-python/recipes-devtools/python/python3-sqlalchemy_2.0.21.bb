DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "http://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b707d50badb798e1d897f2c8f649382d"

SRC_URI[sha256sum] = "05b971ab1ac2994a14c56b35eaaa91f86ba080e9ad481b20d99d77f381bb6258"

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
