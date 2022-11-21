SUMMARY = "The asttokens module annotates Python abstract syntax trees (ASTs)"
HOMEPAGE = "https://github.com/gristlabs/asttokens"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PYPI_PACKAGE = "asttokens"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "4aa76401a151c8cc572d906aad7aea2a841780834a19d780f4321c0fe1b54635"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native"
