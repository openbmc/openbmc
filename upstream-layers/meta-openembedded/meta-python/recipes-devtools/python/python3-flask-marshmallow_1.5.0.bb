SUMMARY = "Flask + marshmallow for beautiful APIs"
HOMEPAGE = "https://github.com/marshmallow-code/flask-marshmallow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27586b20700d7544c06933afe56f7df4"

inherit pypi python_flit_core

PYPI_PACKAGE = "flask_marshmallow"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "7c06b56e41647eccdb3cd57c25b109f19191b4c62509362bd64920cdf601a066"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-marshmallow \
    "
