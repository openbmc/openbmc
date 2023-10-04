SUMMARY = "A python package that provides useful locks."
HOMEPAGE = "https://github.com/harlowja/fasteners"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4476c4be31402271e101d9a4a3430d52"

SRC_URI[sha256sum] = "b4f37c3ac52d8a445af3a66bce57b33b5e90b97c696b7b984f530cf8f0ded09c"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
	${PYTHON_PN}-logging \
	${PYTHON_PN}-fcntl \
"
