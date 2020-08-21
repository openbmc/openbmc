SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=10;endline=10;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[md5sum] = "7ec7c435cc73eefebd9f0af43c120044"
SRC_URI[sha256sum] = "99a22d87add3f634ff917310a3d87e499f19e663413a52eb9232c447aa646c9f"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

