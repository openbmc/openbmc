SUMMARY = "A microframework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "\
Flask is a microframework for Python based on Werkzeug, Jinja 2 and good \
intentions. And before you ask: It’s BSD licensed!"
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "5f873c5184c897c8d9d1b05df1e3d01b14910ce69607a117bd3277098a5836ac"

SRC_URI += " \
        file://run-ptest \
"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/Flask"
UPSTREAM_CHECK_REGEX = "/Flask/(?P<pver>(\d+[\.\-_]*)+)"

inherit pypi python_setuptools_build_meta ptest

CLEANBROKEN = "1"

RDEPENDS:${PN} = " \
    python3-blinker \
    python3-click \
    python3-importlib-metadata \
    python3-itsdangerous \
    python3-jinja2 \
    python3-werkzeug \
"

RDEPENDS:${PN}-ptest += "\
    python3-asgiref \
    python3-pytest \
    python3-unittest-automake-output \
"
do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
