SUMMARY = "A microframework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "\
Flask is a microframework for Python based on Werkzeug, Jinja 2 and good \
intentions. And before you ask: It’s BSD licensed!"
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "cfadcdb638b609361d29ec22360d6070a77d7463dcb3ab08d2c2f2f168845f58"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/Flask"
UPSTREAM_CHECK_REGEX = "/Flask/(?P<pver>(\d+[\.\-_]*)+)"

inherit pypi python_setuptools_build_meta

CLEANBROKEN = "1"

RDEPENDS:${PN} = " \
    ${PYTHON_PN}-blinker \
    ${PYTHON_PN}-click \
    ${PYTHON_PN}-itsdangerous \
    ${PYTHON_PN}-jinja2 \
    ${PYTHON_PN}-profile \
    ${PYTHON_PN}-werkzeug \
"
