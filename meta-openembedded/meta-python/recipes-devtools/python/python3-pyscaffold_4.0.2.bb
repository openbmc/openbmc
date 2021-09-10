SUMMARY = "Python project template generator with batteries included"
DESCRIPTION = "PyScaffold package helps to setup a new Python project. \
After installation, it provides a new command [putup], which could be \
used to create template Projects."

HOMEPAGE = "https://github.com/pyscaffold/pyscaffold"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3

PYPI_PACKAGE = "PyScaffold"

SRC_URI[sha256sum] = "cfa719c2d97392ec3cd5f44d95ec0d40df2dd483d5ee05ea029896cfd7c04af8"

BBCLASSEXTEND = "native nativesdk"
