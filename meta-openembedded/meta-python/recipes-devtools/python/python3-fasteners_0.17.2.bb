SUMMARY = "A python package that provides useful locks."
HOMEPAGE = "https://github.com/harlowja/fasteners"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4476c4be31402271e101d9a4a3430d52"

SRC_URI[sha256sum] = "2aceacb2bd618ce8526676f7a3e84ea25d0165ef10abb574a45b4a9c07292d2e"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	${PYTHON_PN}-logging \
	${PYTHON_PN}-fcntl \
"

do_compile:prepend() {
	echo "from setuptools import setup" > ${S}/setup.py
	echo "setup()" >> ${S}/setup.py
}
