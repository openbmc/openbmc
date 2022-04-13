SUMMARY = "Python project template generator with batteries included"
DESCRIPTION = "PyScaffold package helps to setup a new Python project. \
After installation, it provides a new command [putup], which could be \
used to create template Projects."

HOMEPAGE = "https://github.com/pyscaffold/pyscaffold"
SECTION = "devel/python"
LICENSE = "0BSD & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=14a49c74a1d91829908ac756c07e6b91"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "c8cfa9983503f31b301f4b0bec0a8f41518f9a3a3853471ce94f9f9dc770a3e2"

inherit pypi python_setuptools_build_meta
PYPI_PACKAGE = "PyScaffold"

RDEPENDS:${PN} += " \
	python3-email \
	python3-compression \
"

BBCLASSEXTEND = "native nativesdk"
