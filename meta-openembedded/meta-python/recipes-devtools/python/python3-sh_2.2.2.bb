SUMMARY = "Python subprocess replacement"
HOMEPAGE = "https://github.com/amoffat/sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5317094292296f03405f59ae5f6544b6"

SRC_URI[sha256sum] = "653227a7c41a284ec5302173fbc044ee817c7bad5e6e4d8d55741b9aeb9eb65b"

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
