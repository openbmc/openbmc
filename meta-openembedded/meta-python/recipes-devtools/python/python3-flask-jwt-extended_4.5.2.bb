SUMMARY = "Extended JWT integration with Flask"
HOMEPAGE = "https://github.com/vimalloc/flask-jwt-extended"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9166295d7c482b9440bbb2b5c0fa43ac"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-JWT-Extended"

SRC_URI[sha256sum] = "ba56245ba43b71c8ae936784b867625dce8b9956faeedec2953222e57942fb0b"

RDEPENDS:${PN} += "\
    python3-werkzeug \
    python3-flask \
    python3-pyjwt \
    "
