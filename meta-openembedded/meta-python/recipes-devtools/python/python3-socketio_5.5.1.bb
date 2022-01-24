SUMMARY = "Socket.IO server"
HOMEPAGE = "https://github.com/miguelgrinberg/python-socketio/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42d0a9e728978f0eeb759c3be91536b8"

inherit pypi setuptools3

PYPI_PACKAGE = "python-socketio"

SRC_URI[sha256sum] = "ac8e64d59a15d1c31a4fe8434f4ff16d0f640c824ba517dce7ca99e95f0cd36a"

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
