SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[sha256sum] = "4fb968c43bc384f184cd1a25c1842297c2e3d6efc2f755a61be6d4406858220f"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
