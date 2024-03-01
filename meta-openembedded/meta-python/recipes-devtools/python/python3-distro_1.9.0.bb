SUMMARY = "Distro is an OS platform information API"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

PYPI_PACKAGE = "distro"

SRC_URI[sha256sum] = "2fa77c6fd8940f116ee1d6b94a2f90b13b5ea8d019b98bc8bafdcabcdd9bdbed"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
	python3-core \
	python3-json \
	python3-logging \
	python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
