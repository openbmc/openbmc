SUMMARY = "Bleak is a GATT client software, capable of connecting to BLE devices acting as GATT servers."
HOMEPAGE = "https://github.com/hbldh/bleak"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcbc2069a86cba1b5e47253679f66ed7"

SRCREV = "2e6890f68f6718b4f92e602b9b926aa3d107b556"
PYPI_SRC_URI = "git://github.com/hbldh/bleak.git;protocol=https;branch=develop"

inherit pypi python_poetry_core ptest-python-pytest

S = "${WORKDIR}/git"

RDEPENDS:${PN}-ptest += " \
	python3-pytest-asyncio \
"

RDEPENDS:${PN} += " \
	python3-core (>3.8) \
	python3-dbus-fast \
	python3-xml \
"
