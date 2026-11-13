SUMMARY = "Socket.IO integration for Flask applications"
HOMEPAGE = "https://github.com/miguelgrinberg/Flask-SocketIO/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38cc21254909604298ce763a6e4440a0"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "6b2f967f24f693f2316d108b2d2125642db856b2681c12e60ded9bff1b8035e0"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-socketio \
    "
