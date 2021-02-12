SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[sha256sum] = "5c4319f5214ada20807857dc8fdf3dc7d2afe8d6dd38f5c516c72e2be47d2227"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
