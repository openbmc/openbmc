SUMMARY = "Bleak is a GATT client software, capable of connecting to BLE devices acting as GATT servers."
HOMEPAGE = "https://github.com/hbldh/bleak"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcbc2069a86cba1b5e47253679f66ed7"

SRC_URI:append = " \
	file://run-ptest \
	file://0001-bleak-Support-newer-uv_build-versions.patch \
"

inherit pypi python_poetry_core ptest-python-pytest

SRC_URI[sha256sum] = "c2229cb8238d5876b4bd05c74bf7a1aea1f88da39d2e51ac9dfd5cc319d5265f"

DEPENDS += "\
	python3-uv-build-native \
"

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
