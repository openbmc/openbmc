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
"

SRC_URI[sha256sum] = "f8609e3afdda318fdc336b4ba2de8dd397bb8f9b8a1b43e56c27330e32c2e34c"
