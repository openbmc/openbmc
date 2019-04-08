SUMMARY = "Socket.IO server"
HOMEPAGE = "https://github.com/miguelgrinberg/python-socketio/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42d0a9e728978f0eeb759c3be91536b8"

inherit pypi

PYPI_PACKAGE = "python-socketio"

SRC_URI[md5sum] = "0b57da61a9464d2e0dc9a8d0164d86d2"
SRC_URI[sha256sum] = "64feb0817f1bf7e3e7fc05f6f65e28d76146d8061cb7de25b63502717f80e908"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-engineio \
    "
