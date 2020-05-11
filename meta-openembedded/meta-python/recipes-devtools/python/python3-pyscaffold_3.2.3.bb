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

SRC_URI[md5sum] = "6769d2a26a9d1f3038944b8fbfe728ff"
SRC_URI[sha256sum] = "96c3e7f5529df0b5b351e879a141e1e5c9f26211f30d493c23d8c09d9d610a6f"

BBCLASSEXTEND = "native nativesdk"
