SUMMARY = "A faster version of dbus-next originally from the great DBus next library."
HOMEPAGE = "https://github.com/bluetooth-devices/dbus-fast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=729e372b5ea0168438e4fd4a00a04947"

SRC_URI[sha256sum] = "c98c7dfc3c589d6a5ded3427addfef1f9199525422f976e10d70a5c10558f5bf"

PYPI_PACKAGE = "dbus_fast"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-core (>=3.7) \
    python3-async-timeout \
"
