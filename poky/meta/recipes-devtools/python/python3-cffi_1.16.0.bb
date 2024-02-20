SUMMARY = "Foreign Function Interface for Python calling C code"
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS += "libffi python3-pycparser"

SRC_URI[sha256sum] = "bcb3ef43e58665bbda2fb198698fcae6776483e0c4a631aa5647806c25e02cc0"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target = " \
    python3-ctypes \
    python3-io \
    python3-pycparser \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
