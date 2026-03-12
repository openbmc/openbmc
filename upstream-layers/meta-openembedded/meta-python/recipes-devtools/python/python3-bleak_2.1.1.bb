SUMMARY = "Bleak is a GATT client software, capable of connecting to BLE devices acting as GATT servers."
HOMEPAGE = "https://github.com/hbldh/bleak"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcbc2069a86cba1b5e47253679f66ed7"

SRCREV = "5d76a62a549a4674c64ee151d38e2339e8ba948a"
PYPI_SRC_URI = "git://github.com/hbldh/bleak.git;protocol=https;branch=develop;destsuffix=${S};tag=v${PV}"

SRC_URI:append = " file://run-ptest"

inherit pypi python_poetry_core ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
	python3-asyncio \
	python3-pytest-asyncio \
	python3-pytest-cov \
	python3-bumble \
"

RDEPENDS:${PN} += " \
	python3-core (>3.8) \
	python3-dbus-fast \
	python3-xml \
"
