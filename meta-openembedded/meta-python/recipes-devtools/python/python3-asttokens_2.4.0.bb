SUMMARY = "The asttokens module annotates Python abstract syntax trees (ASTs)"
HOMEPAGE = "https://github.com/gristlabs/asttokens"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PYPI_PACKAGE = "asttokens"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "2e0171b991b2c959acc6c49318049236844a5da1d65ba2672c4880c1c894834e"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native"
