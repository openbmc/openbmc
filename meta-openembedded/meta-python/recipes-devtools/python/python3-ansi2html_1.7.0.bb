DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/ralphbean/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "GPLv3"

PYPI_PACKAGE = "ansi2html"

SRC_URI[sha256sum] = "69316be8c68ac91c5582d397c2890e69c993cc7cda52062ac7e45fcb660d8edc"

inherit pypi setuptools3

DEPENDS += " \
	${PYTHON_PN}-setuptools-scm-native \
	${PYTHON_PN}-toml-native \
"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-six \
	${PYTHON_PN}-compression \
"

do_compile:prepend() {
	echo "from setuptools import setup" > ${S}/setup.py
	echo "setup()" >> ${S}/setup.py
}
