SUMMARY = "Socket.IO server"
HOMEPAGE = "https://github.com/miguelgrinberg/python-socketio/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42d0a9e728978f0eeb759c3be91536b8"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "python_socketio"

SRC_URI[sha256sum] = "ad88c228d921646efa436c0a0df217e364ef30ec072df4041484e54d49c15989"

PACKAGECONFIG ?= "asyncio_client client"
PACKAGECONFIG[asyncio_client] = ",,,python3-aiohttp python3-websockets"
PACKAGECONFIG[client] = ",,,python3-requests python3-websocket-client"

RDEPENDS:${PN} += "\
    python3-engineio \
    python3-logging \
    python3-math \
    python3-pickle \
    python3-json \
    python3-threading \
    python3-six \
    python3-attrs \
    python3-bidict \
    "
