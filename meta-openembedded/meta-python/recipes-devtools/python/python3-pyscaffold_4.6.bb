SUMMARY = "Python project template generator with batteries included"
DESCRIPTION = "PyScaffold package helps to setup a new Python project. \
After installation, it provides a new command [putup], which could be \
used to create template Projects."

HOMEPAGE = "https://github.com/pyscaffold/pyscaffold"
SECTION = "devel/python"
LICENSE = "0BSD & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=14a49c74a1d91829908ac756c07e6b91"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "4085b8de9200b9f319df6f8ea7997288f159a8e4b284b062d9b2a4d6a9c1234c"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
	python3-email \
	python3-compression \
"

BBCLASSEXTEND = "native nativesdk"
