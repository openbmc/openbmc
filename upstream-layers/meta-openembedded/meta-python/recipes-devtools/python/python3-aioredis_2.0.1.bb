SUMMARY = "The library is intended to provide simple and clear interface to Redis based on asyncio."
HOMEPAGE = "https://github.com/aio-libs/aioredis-py"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bf9085f32a671dfa86ee69fe0fff7b95"

SRC_URI[sha256sum] = "eaa51aaf993f2d71f54b70527c440437ba65340588afeb786cd87c55c89cd98e"

PYPI_PACKAGE = "aioredis"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-core (>=3.6) \
    python3-async-timeout \
    python3-typing-extensions \
"
