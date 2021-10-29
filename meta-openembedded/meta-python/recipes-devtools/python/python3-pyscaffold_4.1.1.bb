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

SRC_URI[sha256sum] = "8972252fda90d1020a93f1e99db370c002d18f52ff2fca9c3cc2a0aee74d07ad"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
	python3-email \
	python3-compression \
"
