SUMMARY = "A faster version of dbus-next originally from the great DBus next library."
HOMEPAGE = "https://github.com/bluetooth-devices/dbus-fast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=729e372b5ea0168438e4fd4a00a04947"

SRC_URI[sha256sum] = "a4a5dddc04b1ade5eb7650d791e2f6fb7c1334595593473914e78a2526ecddda"

inherit pypi python_poetry_core cython

# Remove when https://github.com/Bluetooth-Devices/dbus-fast/issues/825 is closed
DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += "python3-core"
