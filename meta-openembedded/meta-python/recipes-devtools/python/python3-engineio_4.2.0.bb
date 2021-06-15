SUMMARY = "Engine.IO server"
HOMEPAGE = "https://github.com/miguelgrinberg/python-engineio/"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42d0a9e728978f0eeb759c3be91536b8"

inherit pypi setuptools3

PYPI_PACKAGE = "python-engineio"

RDEPENDS_${PN} += " \
	python3-netclient \
	python3-json \
	python3-logging \
	python3-compression \
	python3-asyncio \
"

SRC_URI[md5sum] = "1fa937ec2a9f6feac27e9f65824c5781"
SRC_URI[sha256sum] = "4e97c1189c23923858f5bb6dc47cfcd915005383c3c039ff01c89f2c00d62077"
