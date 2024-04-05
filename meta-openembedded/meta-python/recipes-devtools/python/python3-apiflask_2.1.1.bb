SUMMARY = "APIFlask is a lightweight Python web API framework based on Flask and marshmallow-code projects."
HOMEPAGE = "https://github.com/apiflask/apiflask"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f89d1b0dec37448d4f4163dc3c40e64"

inherit pypi setuptools3

PYPI_PACKAGE = "APIFlask"

SRC_URI[sha256sum] = "88db5a539cc155e35d9636d99b434d00ca6c0b23e7c87c8321ec9dc980535366"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-flask-marshmallow \
    python3-webargs \
    python3-flask-httpauth \
    python3-apispec \
    "
