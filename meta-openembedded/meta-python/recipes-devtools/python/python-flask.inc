SUMMARY = "A microframework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "\
Flask is a microframework for Python based on Werkzeug, Jinja 2 and good \
intentions. And before you ask: It’s BSD licensed!"
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c011883ac26229b8ba3084f55c7664c6"

SRC_URI[md5sum] = "824f0f20aae1f44c9c7dc4054adb7969"
SRC_URI[sha256sum] = "2271c0070dbcb5275fad4a82e29f23ab92682dc45f9dfbc22c02ba9b9322ce48"

CLEANBROKEN = "1"

PYPI_PACKAGE = "Flask"

RDEPENDS_${PN} = "${PYTHON_PN}-werkzeug ${PYTHON_PN}-werkzeug-tests ${PYTHON_PN}-jinja2 ${PYTHON_PN}-itsdangerous ${PYTHON_PN}-click"
