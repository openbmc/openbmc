SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi

PYPI_PACKAGE = "Flask-SocketIO"

SRC_URI[md5sum] = "b23222fb7dd2f0676d78bbe24153fd80"
SRC_URI[sha256sum] = "2172dff1e42415ba480cee02c30c2fc833671ff326f1598ee3d69aa02cf768ec"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-socketio \
    "
