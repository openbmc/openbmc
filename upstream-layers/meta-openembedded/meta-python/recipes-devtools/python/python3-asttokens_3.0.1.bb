SUMMARY = "The asttokens module annotates Python abstract syntax trees (ASTs)"
HOMEPAGE = "https://github.com/gristlabs/asttokens"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d9b931fa23ab1cacd0087f9e2ee12c0"

PYPI_PACKAGE = "asttokens"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "71a4ee5de0bde6a31d64f6b13f2293ac190344478f081c3d1bccfcf5eacb0cb7"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-six \
"

BBCLASSEXTEND = "native nativesdk"
