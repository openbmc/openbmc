SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "flask_socketio"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "fe5bd995c3ed4da9a98f335d0d830fa1a19d84a64789f6265642a671fdacaeac"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-socketio \
    "
