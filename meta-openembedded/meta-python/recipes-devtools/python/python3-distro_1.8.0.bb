SUMMARY = "Distro is an OS platform information API"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

PYPI_PACKAGE = "distro"

SRC_URI[sha256sum] = "02e111d1dc6a50abb8eed6bf31c3e48ed8b0830d1ea2a1b78c61765c2513fdd8"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "\
	${PYTHON_PN}-core \
	${PYTHON_PN}-json \
	${PYTHON_PN}-logging \
	${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
