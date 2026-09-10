SUMMARY = "The asttokens module annotates Python abstract syntax trees (ASTs)"
HOMEPAGE = "https://github.com/gristlabs/asttokens"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d9b931fa23ab1cacd0087f9e2ee12c0"


inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "3ecdbd8f2cc195f53ccada3a613538bb5f9ef6f6869129f13e03c30a677b8fe2"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-six \
"

BBCLASSEXTEND = "native nativesdk"
