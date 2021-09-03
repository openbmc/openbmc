SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=10;endline=10;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "e2ef7239991699e3355d54f8e968a21bb940a1dbf34a4d226741e64462516fad"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

