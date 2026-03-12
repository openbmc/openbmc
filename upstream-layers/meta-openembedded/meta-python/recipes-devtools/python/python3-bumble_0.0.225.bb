SUMMARY = "Bluetooth Stack for Apps, Emulation, Test and Experimentation"
HOMEPAGE = "https://github.com/google/bumble"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a775f1b11285b6abedd76748d176125"

SRC_URI[sha256sum] = "a69455e9b35a80e4b9ac555b749c9371039fd0a7aa88116bb3fea07204199ed8"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
	libusb1 \
	python3-appdirs \
	python3-click \
	python3-cryptography \
	python3-grpcio \
	python3-humanize \
	python3-platformdirs \
	python3-prettytable \
	python3-prompt-toolkit \
	python3-protobuf \
	python3-pyee \
	python3-pyserial \
	python3-pyserial-asyncio \
	python3-pyusb \
	python3-websockets \
"
RDEPENDS:${PN}-ptest += "\
	python3-pytest-asyncio \
	python3-aiohttp \
"
# WARNING: We were unable to map the following python package/module
# runtime dependencies to the bitbake packages which include them:
#    libusb-package
#    pyee

PYPI_PACKAGE = "bumble"
