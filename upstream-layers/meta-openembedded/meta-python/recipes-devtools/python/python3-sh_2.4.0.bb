SUMMARY = "Python subprocess replacement"
HOMEPAGE = "https://github.com/amoffat/sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5317094292296f03405f59ae5f6544b6"

SRC_URI[sha256sum] = "a250aef68509ed93419c9a1d90b0647cd5cbe26107ba94d3717ef5b6d595ffd9"

inherit pypi python_hatchling

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
