SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[sha256sum] = "11d1d78b8805cda351b27828a110b88c74a573be62b07f7f5a519fb67fae0a58"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
