SUMMARY = "A python package that provides useful locks."
HOMEPAGE = "https://github.com/harlowja/fasteners"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4476c4be31402271e101d9a4a3430d52"

SRC_URI[sha256sum] = "a9a42a208573d4074c77d041447336cf4e3c1389a256fd3e113ef59cf29b7980"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
	${PYTHON_PN}-logging \
	${PYTHON_PN}-fcntl \
"
