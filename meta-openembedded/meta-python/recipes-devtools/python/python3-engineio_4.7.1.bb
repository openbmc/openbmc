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

SRC_URI[sha256sum] = "a8422e345cd9a21451303380b160742ff02197975b1c3a02cef115febe2b1b20"
