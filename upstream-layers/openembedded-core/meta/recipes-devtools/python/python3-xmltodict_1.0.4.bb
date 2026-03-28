SUMMARY = "Makes working with XML feel like you are working with JSON"
HOMEPAGE = "https://github.com/martinblech/xmltodict"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01441d50dc74476db58a41ac10cb9fa2"

SRC_URI[sha256sum] = "6d94c9f834dd9e44514162799d344d815a3a4faec913717a9ecbfa5be1bb8e61"

PYPI_PACKAGE = "xmltodict"

BBCLASSEXTEND = "native nativesdk"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
	python3-core \
	python3-xml \
	python3-io \
"

