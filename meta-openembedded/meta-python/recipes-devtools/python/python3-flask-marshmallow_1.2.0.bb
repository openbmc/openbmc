SUMMARY = "Flask + marshmallow for beautiful APIs"
HOMEPAGE = "https://github.com/marshmallow-code/flask-marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27586b20700d7544c06933afe56f7df4"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "flask_marshmallow"

SRC_URI[sha256sum] = "d0f79eb9743f0c530a3d9e848503e1f2228e6b35a819c91e913af02e68421805"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-marshmallow \
    "
