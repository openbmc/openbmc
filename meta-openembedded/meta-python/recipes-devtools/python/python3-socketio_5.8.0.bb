SUMMARY = "Socket.IO server"
HOMEPAGE = "https://github.com/miguelgrinberg/python-socketio/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42d0a9e728978f0eeb759c3be91536b8"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "python-socketio"

SRC_URI[sha256sum] = "e714f4dddfaaa0cb0e37a1e2deef2bb60590a5b9fea9c343dd8ca5e688416fd9"

PACKAGECONFIG ?= "asyncio_client client"
PACKAGECONFIG[asyncio_client] = ",,,${PYTHON_PN}-aiohttp ${PYTHON_PN}-websockets"
PACKAGECONFIG[client] = ",,,${PYTHON_PN}-requests ${PYTHON_PN}-websocket-client"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-engineio \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-bidict \
    "
