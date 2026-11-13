SUMMARY = "Engine.IO server"
HOMEPAGE = "https://github.com/miguelgrinberg/python-engineio/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42d0a9e728978f0eeb759c3be91536b8"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "python-engineio"

RDEPENDS:${PN} += " \
	python3-netclient \
	python3-json \
	python3-logging \
	python3-compression \
	python3-asyncio \
	python3-io \
	python3-simple-websocket \
	python3-threading \
"

SRC_URI[sha256sum] = "1a412e798a90c64fabe7b20798939ae6d4a03f34da8a33d5a21a25a8bbbc3612"
