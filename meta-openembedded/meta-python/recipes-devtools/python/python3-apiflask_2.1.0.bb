SUMMARY = "APIFlask is a lightweight Python web API framework based on Flask and marshmallow-code projects."
HOMEPAGE = "https://github.com/apiflask/apiflask"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f89d1b0dec37448d4f4163dc3c40e64"

inherit pypi setuptools3

PYPI_PACKAGE = "APIFlask"

SRC_URI[sha256sum] = "e7616d902d446eb9e1c67d1d8a34691b437f9da4fe7a3b4d49c91ba88c85ee2a"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-flask-marshmallow \
    python3-webargs \
    python3-flask-httpauth \
    python3-apispec \
    "
