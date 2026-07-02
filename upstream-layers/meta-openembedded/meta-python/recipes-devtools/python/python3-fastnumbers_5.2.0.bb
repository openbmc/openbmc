SUMMARY = "Super-fast and clean conversions to numbers."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32431d1b650010945da4e078011c8fa"

PYPI_PACKAGE = "fastnumbers"

SRC_URI[sha256sum] = "07266a2fca9e08eeb5a6c70be4c8db3637db9b930ea5198facbbb9b0b31d4d03"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN}-ptest += "\
						 python3-ctypes \
						 python3-hypothesis \
						 python3-numpy \
						 python3-typing-extensions \
						 "
