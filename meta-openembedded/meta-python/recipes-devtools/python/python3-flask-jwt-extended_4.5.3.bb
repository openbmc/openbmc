SUMMARY = "Extended JWT integration with Flask"
HOMEPAGE = "https://github.com/vimalloc/flask-jwt-extended"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9166295d7c482b9440bbb2b5c0fa43ac"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-JWT-Extended"

SRC_URI[sha256sum] = "061ef3d25ed5743babe4964ab38f36d870e6d2fd8a126bab5d77ddef8a01932b"

RDEPENDS:${PN} += "\
    python3-werkzeug \
    python3-flask \
    python3-pyjwt \
    "
