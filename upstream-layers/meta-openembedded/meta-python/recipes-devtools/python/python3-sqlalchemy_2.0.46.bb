DESCRIPTION = "Python SQL toolkit and Object Relational Mapper that gives \
application developers the full power and flexibility of SQL"
HOMEPAGE = "https://www.sqlalchemy.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=53a9111717b305b0fae0d704a24925c3"

SRC_URI[sha256sum] = "cf36851ee7219c170bb0793dbc3da3e80c582e04a5437bc601bfe8c85c9216d7"

inherit pypi python_setuptools_build_meta cython

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
    python3-greenlet \
"

CVE_PRODUCT = "sqlalchemy"

BBCLASSEXTEND = "native nativesdk"
