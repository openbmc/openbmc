SUMMARY = "Socket.IO server"
HOMEPAGE = "https://github.com/miguelgrinberg/python-socketio/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42d0a9e728978f0eeb759c3be91536b8"

inherit pypi setuptools3

PYPI_PACKAGE = "python-socketio"

SRC_URI[md5sum] = "44bb7854234e144655ef9c34b0165704"
SRC_URI[sha256sum] = "358d8fbbc029c4538ea25bcaa283e47f375be0017fcba829de8a3a731c9df25a"

PACKAGECONFIG ?= "asyncio_client client"
PACKAGECONFIG[asyncio_client] = ",,,${PYTHON_PN}-aiohttp ${PYTHON_PN}-websockets"
PACKAGECONFIG[client] = ",,,${PYTHON_PN}-requests ${PYTHON_PN}-websocket-client"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-engineio \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-attrs \
    "
