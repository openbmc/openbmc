SUMMARY = "Python interface to libarchive"
DESCRIPTION = "A Python interface to libarchive. It uses the standard ctypes module to \
    dynamically load and access the C library."
HOMEPAGE = "https://github.com/Changaco/python-libarchive-c"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=bcab380227a83bc147350b40a81e6ffc"

PYPI_PACKAGE = "libarchive-c"

inherit pypi setuptools3

SRC_URI[sha256sum] = "7bcce24ea6c0fa3bc62468476c6d2f6264156db2f04878a372027c10615a2721"

RDEPENDS:${PN} += "\
  libarchive \
  python3-ctypes \
  python3-mmap \
  python3-logging \
"

BBCLASSEXTEND = "native"
