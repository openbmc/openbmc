SUMMARY = "The asttokens module annotates Python abstract syntax trees (ASTs)"
HOMEPAGE = "https://github.com/gristlabs/asttokens"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PYPI_PACKAGE = "asttokens"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "4622110b2a6f30b77e1473affaa97e711bc2f07d3f10848420ff1898edbe94f3"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native"
