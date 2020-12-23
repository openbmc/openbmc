SUMMARY = "A zero-dependency DBus library for Python with asyncio support"
HOMEPAGE = "https://github.com/acrisci/python-dbus-next"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94e750c96e56788499b56c81de91431c"

SRC_URI[md5sum] = "0d44e12e8689637a0c048ec7bb51d842"
SRC_URI[sha256sum] = "1b1942bffcc8c9a5bd6834257df227a55ee28e07dd413ead82ddd23115652363"

PYPI_PACKAGE = "dbus_next"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
