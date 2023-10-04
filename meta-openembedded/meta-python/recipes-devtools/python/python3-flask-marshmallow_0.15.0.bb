SUMMARY = "Flask + marshmallow for beautiful APIs"
HOMEPAGE = "https://github.com/marshmallow-code/flask-marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c3ff8567ef1f2a8cf223f30ac5a6f094"

inherit pypi setuptools3

SRC_URI[sha256sum] = "2083ae55bebb5142fff98c6bbd483a2f5dbc531a8bc1be2180ed5f75e7f3fccc"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-marshmallow \
    python3-packaging \
    "
