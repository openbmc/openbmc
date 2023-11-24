SUMMARY = "APIFlask is a lightweight Python web API framework based on Flask and marshmallow-code projects."
HOMEPAGE = "https://github.com/apiflask/apiflask"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f89d1b0dec37448d4f4163dc3c40e64"

inherit pypi setuptools3

PYPI_PACKAGE = "APIFlask"

SRC_URI[sha256sum] = "7ffe29e082c6cc76d8ae78ba2445b5fcd69092fac04f4f8cd23b1c887cb291cc"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-flask-marshmallow \
    python3-webargs \
    python3-flask-httpauth \
    python3-apispec \
    "
