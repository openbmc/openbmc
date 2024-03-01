DESCRIPTION = "Bluetooth Python extension module"
HOMEPAGE = "https://pybluez.github.io/"
SECTION = "devel/python"

DEPENDS = "bluez5"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8a71d0475d08eee76d8b6d0c6dbec543"

SRC_URI += "file://0001-Use-Py_ssize_t-when-parsing-buffer-length-fix-426-42.patch \
            file://py-3.11.patch "
SRC_URI[md5sum] = "afbe8429bb82d2c46a3d0f5f4f898f9d"
SRC_URI[sha256sum] = "c8f04d2e78951eaa9de486b4d49381704e8943d0a6e6e58f55fcd7b8582e90de"

PYPI_PACKAGE = "PyBluez"

inherit pypi setuptools3

do_configure:prepend() {
   sed -i -e "/use_2to3=True,/d" ${S}/setup.py
}

RDEPENDS:${PN} += "\
    bluez5 \
    python3-fcntl \
"
