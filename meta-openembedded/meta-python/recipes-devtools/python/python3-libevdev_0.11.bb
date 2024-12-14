DESCRIPTION = "python-libevdev is a Python wrapper around the libevdev C library."
HOMEPAGE = "https://gitlab.freedesktop.org/libevdev/python-libevdev"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d94c10c546b419eddc6296157ec40747"

SRC_URI[sha256sum] = "e9ca006a4df2488a60bd9a740011ee948d81904be2364f017e560169508f560f"

inherit pypi setuptools3

PYPI_PACKAGE = "libevdev"

RDEPENDS:${PN} += " \
    libevdev \
    python3-ctypes \
"
