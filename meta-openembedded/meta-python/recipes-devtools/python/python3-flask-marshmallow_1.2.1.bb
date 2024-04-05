SUMMARY = "Flask + marshmallow for beautiful APIs"
HOMEPAGE = "https://github.com/marshmallow-code/flask-marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27586b20700d7544c06933afe56f7df4"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "flask_marshmallow"

SRC_URI[sha256sum] = "00ee96399ed664963afff3b5d6ee518640b0f91dbc2aace2b5abcf32f40ef23a"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-marshmallow \
    "
