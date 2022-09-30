SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[sha256sum] = "fd0ed0fc1341671d92d5f5b2f5503916deb7aa7e2940e6636cfa2c087c828bf9"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
