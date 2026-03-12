SUMMARY = "Extended JWT integration with Flask"
HOMEPAGE = "https://github.com/vimalloc/flask-jwt-extended"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9166295d7c482b9440bbb2b5c0fa43ac"

inherit pypi setuptools3

PYPI_PACKAGE = "flask_jwt_extended"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "8085d6757505b6f3291a2638c84d207e8f0ad0de662d1f46aa2f77e658a0c976"

RDEPENDS:${PN} += "\
    python3-werkzeug \
    python3-flask \
    python3-pyjwt \
    "
