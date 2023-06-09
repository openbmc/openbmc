DESCRIPTION = "python-libevdev is a Python wrapper around the libevdev C library."
HOMEPAGE = "https://gitlab.freedesktop.org/libevdev/python-libevdev"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[md5sum] = "34b48098c1fba26de79a0d67a17a588a"
SRC_URI[sha256sum] = "e9ca006a4df2488a60bd9a740011ee948d81904be2364f017e560169508f560f"

inherit pypi setuptools3

PYPI_PACKAGE = "libevdev"

RDEPENDS:${PN} += " \
    libevdev \
    python3-ctypes \
"
