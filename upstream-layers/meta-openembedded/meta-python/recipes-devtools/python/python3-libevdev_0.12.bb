DESCRIPTION = "python-libevdev is a Python wrapper around the libevdev C library."
HOMEPAGE = "https://gitlab.freedesktop.org/libevdev/python-libevdev"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d94c10c546b419eddc6296157ec40747"

SRC_URI[sha256sum] = "02e952632ec6c249cbb9c66f6fa00012ea448b06606c77cd139133bc2fe46b08"

inherit pypi setuptools3

PYPI_PACKAGE = "libevdev"

RDEPENDS:${PN} += " \
    libevdev \
    python3-ctypes \
"
