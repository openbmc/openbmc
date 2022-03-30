DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/ralphbean/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "GPL-3.0-only"

PYPI_PACKAGE = "ansi2html"

SRC_URI[sha256sum] = "69316be8c68ac91c5582d397c2890e69c993cc7cda52062ac7e45fcb660d8edc"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	${PYTHON_PN}-setuptools-scm-native \
	${PYTHON_PN}-toml-native \
"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-six \
	${PYTHON_PN}-compression \
"
