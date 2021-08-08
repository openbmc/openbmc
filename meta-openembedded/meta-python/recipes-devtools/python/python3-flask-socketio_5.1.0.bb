SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[sha256sum] = "b41b9f6fb0d7f3fcadd54c44653307a9b96e985c7da73f92779480248b5b6874"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
