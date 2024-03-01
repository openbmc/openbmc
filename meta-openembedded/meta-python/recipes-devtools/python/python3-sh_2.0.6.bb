SUMMARY = "Python subprocess replacement"
HOMEPAGE = "https://github.com/amoffat/sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5317094292296f03405f59ae5f6544b6"

SRC_URI[sha256sum] = "9b2998f313f201c777e2c0061f0b1367497097ef13388595be147e2a00bf7ba1"

PYPI_PACKAGE = "sh"

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-codecs \
    python3-compression \
    python3-core \
    python3-io \
    python3-logging \
    python3-resource \
    python3-shell \
    python3-terminal \
    python3-threading \
    python3-unixadmin \
    python3-fcntl \
"
