SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[md5sum] = "298965a43f6534e8a5b24d1ba1fc4186"
SRC_URI[sha256sum] = "8d8f9f104db5ddff1b06ba322d8e158881d590144199c993fe26cf53218c7edd"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
