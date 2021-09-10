SUMMARY = "A microframework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "\
Flask is a microframework for Python based on Werkzeug, Jinja 2 and good \
intentions. And before you ask: It’s BSD licensed!"
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "1c4c257b1892aec1398784c63791cbaa43062f1f7aeb555c4da961b20ee68f55"

PYPI_PACKAGE = "Flask"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} = " \
    ${PYTHON_PN}-werkzeug \
    ${PYTHON_PN}-jinja2 \
    ${PYTHON_PN}-itsdangerous \
    ${PYTHON_PN}-click \
    ${PYTHON_PN}-profile \
"
