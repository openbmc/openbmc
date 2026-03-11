SUMMARY = "Flask + marshmallow for beautiful APIs"
HOMEPAGE = "https://github.com/marshmallow-code/flask-marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27586b20700d7544c06933afe56f7df4"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "flask_marshmallow"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "27a35d0ce5dcba161cc5f2f4764afbc2536c93fa439a793250b827835e3f3be6"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-marshmallow \
    "
