SUMMARY = "Python interface to libarchive"
DESCRIPTION = "A Python interface to libarchive. It uses the standard ctypes module to \
    dynamically load and access the C library."
HOMEPAGE = "https://github.com/Changaco/python-libarchive-c"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=bcab380227a83bc147350b40a81e6ffc"

PYPI_PACKAGE = "libarchive-c"

inherit pypi setuptools3

SRC_URI[md5sum] = "083bd2cb0043c1e22a52cb9a05e31532"
SRC_URI[sha256sum] = "9919344cec203f5db6596a29b5bc26b07ba9662925a05e24980b84709232ef60"

RDEPENDS_${PN} += "\
  libarchive \
  ${PYTHON_PN}-ctypes \
  ${PYTHON_PN}-mmap \
"

BBCLASSEXTEND = "native"
