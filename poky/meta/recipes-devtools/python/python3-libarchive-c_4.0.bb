SUMMARY = "Python interface to libarchive"
DESCRIPTION = "A Python interface to libarchive. It uses the standard ctypes module to \
    dynamically load and access the C library."
HOMEPAGE = "https://github.com/Changaco/python-libarchive-c"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=bcab380227a83bc147350b40a81e6ffc"

PYPI_PACKAGE = "libarchive-c"

inherit pypi setuptools3

SRC_URI[sha256sum] = "a5b41ade94ba58b198d778e68000f6b7de41da768de7140c984f71d7fa8416e5"

RDEPENDS:${PN} += "\
  libarchive \
  ${PYTHON_PN}-ctypes \
  ${PYTHON_PN}-mmap \
  ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native"
