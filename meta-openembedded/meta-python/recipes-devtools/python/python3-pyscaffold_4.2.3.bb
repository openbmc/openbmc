SUMMARY = "Python project template generator with batteries included"
DESCRIPTION = "PyScaffold package helps to setup a new Python project. \
After installation, it provides a new command [putup], which could be \
used to create template Projects."

HOMEPAGE = "https://github.com/pyscaffold/pyscaffold"
SECTION = "devel/python"
LICENSE = "0BSD & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=14a49c74a1d91829908ac756c07e6b91"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "58c7d9ce296cc26ac377061365c2f87cd8e28f46e4fb2c96ee48f9c64e33ac4a"

inherit pypi python_setuptools_build_meta
PYPI_PACKAGE = "PyScaffold"

RDEPENDS:${PN} += " \
	python3-email \
	python3-compression \
"

BBCLASSEXTEND = "native nativesdk"
