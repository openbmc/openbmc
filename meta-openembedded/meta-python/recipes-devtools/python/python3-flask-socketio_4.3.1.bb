SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[md5sum] = "d7992b0b4aaf473b7eff21fc14e8d1b2"
SRC_URI[sha256sum] = "36c1d5765010d1f4e4f05b4cc9c20c289d9dc70698c88d1addd0afcfedc5b062"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
