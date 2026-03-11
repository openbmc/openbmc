SUMMARY = "A faster version of dbus-next originally from the great DBus next library."
HOMEPAGE = "https://github.com/bluetooth-devices/dbus-fast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=729e372b5ea0168438e4fd4a00a04947"

SRC_URI += "file://0001-pyproject.toml-Remove-upper-version-constraint-for-C.patch"
SRC_URI[sha256sum] = "b027e96c39ed5622bb54d811dcdbbe9d9d6edec3454808a85a1ceb1867d9e25c"

PYPI_PACKAGE = "dbus_fast"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_poetry_core cython

RDEPENDS:${PN} += " \
    python3-core (>=3.7) \
    python3-async-timeout \
"
