SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[sha256sum] = "1efdaacc7a26e94f2b197a80079b1058f6aa644a6094c0a322349e2b9c41f6b1"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
