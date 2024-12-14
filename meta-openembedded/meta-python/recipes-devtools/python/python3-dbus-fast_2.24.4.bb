SUMMARY = "A faster version of dbus-next originally from the great DBus next library."
HOMEPAGE = "https://github.com/bluetooth-devices/dbus-fast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=729e372b5ea0168438e4fd4a00a04947"

SRC_URI[sha256sum] = "58f97e8342d6cd11ebb2c8ac959c5bb342eb83e29180528690b323a5a5def41c"

PYPI_PACKAGE = "dbus_fast"

inherit pypi python_poetry_core cython

RDEPENDS:${PN} += " \
    python3-core (>=3.7) \
    python3-async-timeout \
"
