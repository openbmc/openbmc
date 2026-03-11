SUMMARY = "The asttokens module annotates Python abstract syntax trees (ASTs)"
HOMEPAGE = "https://github.com/gristlabs/asttokens"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PYPI_PACKAGE = "asttokens"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "0dcd8baa8d62b0c1d118b399b2ddba3c4aff271d0d7a9e0d4c1681c79035bbc7"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-six \
"

BBCLASSEXTEND = "native"
