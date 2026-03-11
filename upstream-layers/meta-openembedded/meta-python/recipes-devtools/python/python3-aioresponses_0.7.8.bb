SUMMARY = "Mock out requests made by ClientSession from aiohttp package"
HOMEPAGE = "https://github.com/pnuckowski/aioresponses"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b22b40d5974300051216633098387c57"

SRC_URI[sha256sum] = "b861cdfe5dc58f3b8afac7b0a6973d5d7b2cb608dd0f6253d16b8ee8eaf6df11"

DEPENDS += "python3-pbr-native"

inherit setuptools3 pypi

RDEPENDS:${PN} += "python3-aiohttp \
                   python3-asyncio \
                   python3-core \
                   python3-json \
                   python3-math \
                   python3-multidict \
                   python3-netclient \
                   python3-packaging \
                   python3-unittest \
                   python3-yarl"

# optionally needs asynctest asynctest.case ddt but recipes do not exist for them

PYPI_PACKAGE = "aioresponses"
