SUMMARY = "A microframework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "\
Flask is a microframework for Python based on Werkzeug, Jinja 2 and good \
intentions. And before you ask: It’s BSD licensed!"
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19866b76b054ab30c270c0eb9d7e43d7"

SRC_URI[md5sum] = "97278dfdafda98ba7902e890b0289177"
SRC_URI[sha256sum] = "49f44461237b69ecd901cc7ce66feea0319b9158743dd27a2899962ab214dac1"

CLEANBROKEN = "1"

PYPI_PACKAGE = "Flask"

RDEPENDS_${PN} = "${PYTHON_PN}-werkzeug ${PYTHON_PN}-jinja2 ${PYTHON_PN}-itsdangerous ${PYTHON_PN}-click"
