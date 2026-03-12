SUMMARY = "Makes working with XML feel like you are working with JSON"
HOMEPAGE = "https://github.com/martinblech/xmltodict"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01441d50dc74476db58a41ac10cb9fa2"

SRC_URI[sha256sum] = "3bf1f49c7836df34cf6d9cc7e690c4351f7dfff2ab0b8a1988bba4a9b9474909"

PYPI_PACKAGE = "xmltodict"

BBCLASSEXTEND = "native nativesdk"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
	python3-core \
	python3-xml \
	python3-io \
"

