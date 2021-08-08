SUMMARY = "Python interface to libarchive"
DESCRIPTION = "A Python interface to libarchive. It uses the standard ctypes module to \
    dynamically load and access the C library."
HOMEPAGE = "https://github.com/Changaco/python-libarchive-c"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=bcab380227a83bc147350b40a81e6ffc"

PYPI_PACKAGE = "libarchive-c"

inherit pypi setuptools3

SRC_URI[md5sum] = "8c62da42a8b9bd24642e5430427e6f5a"
SRC_URI[sha256sum] = "618a7ecfbfb58ca15e11e3138d4a636498da3b6bc212811af158298530fbb87e"

RDEPENDS:${PN} += "\
  libarchive \
  ${PYTHON_PN}-ctypes \
  ${PYTHON_PN}-mmap \
  ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native"
