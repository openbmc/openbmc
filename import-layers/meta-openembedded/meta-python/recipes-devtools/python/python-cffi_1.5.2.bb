SUMMARY = "Foreign Function Interface for Python calling C code"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS = "libffi python-pycparser"

SRC_URI[md5sum] = "fa766133f7299464c8bf857e0c966a82"
SRC_URI[sha256sum] = "da9bde99872e46f7bb5cff40a9b1cc08406765efafb583c704de108b6cb821dd"

inherit pypi setuptools

BBCLASSEXTEND = "native"
