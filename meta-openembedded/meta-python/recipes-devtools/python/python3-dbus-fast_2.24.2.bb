SUMMARY = "A faster version of dbus-next originally from the great DBus next library."
HOMEPAGE = "https://github.com/bluetooth-devices/dbus-fast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=729e372b5ea0168438e4fd4a00a04947"

SRC_URI[sha256sum] = "20c517dc70dbc940b8bf2e0f079abf3dfe821fdda3013641a17e3d8f18e131ae"

PYPI_PACKAGE = "dbus_fast"

inherit pypi python_poetry_core

DEPENDS += " \
	python3-setuptools-native \
	python3-wheel-native \
	python3-cython-native \
	"

RDEPENDS:${PN} += " \
    python3-core (>=3.7) \
    python3-async-timeout \
"
