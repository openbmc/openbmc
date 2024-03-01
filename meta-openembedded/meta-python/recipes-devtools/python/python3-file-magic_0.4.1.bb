SUMMARY = "Python front end for libmagic"
DESCRIPTION = "This library is a Python ctypes interface to libmagic."
HOMEPAGE = "https://darwinsys.com/file/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e949c0b3fb4fd86232f00c6ee0bdef3"

SRC_URI[sha256sum] = "a91d1483117f7ed48cd0238ad9be36b04824d57e9c38ea7523113989e81b9c53"

PYPI_PACKAGE="file-magic"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    file \
    python3-core \
    python3-ctypes \
    python3-threading \
"

BBCLASSEXTEND = "native nativesdk"
