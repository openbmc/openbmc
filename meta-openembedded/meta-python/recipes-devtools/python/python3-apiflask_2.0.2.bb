SUMMARY = "APIFlask is a lightweight Python web API framework based on Flask and marshmallow-code projects."
HOMEPAGE = "https://github.com/apiflask/apiflask"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f89d1b0dec37448d4f4163dc3c40e64"

inherit pypi setuptools3

PYPI_PACKAGE = "APIFlask"

SRC_URI[sha256sum] = "c1ab81640a1ab252888e2cc7ae556272a169b449c582abae309a8fe295f9337d"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-flask-marshmallow \
    python3-webargs \
    python3-flask-httpauth \
    python3-apispec \
    "
