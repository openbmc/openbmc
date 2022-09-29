DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/ralphbean/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "GPL-3.0-only"

PYPI_PACKAGE = "ansi2html"

SRC_URI[sha256sum] = "38b82a298482a1fa2613f0f9c9beb3db72a8f832eeac58eb2e47bf32cd37f6d5"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	${PYTHON_PN}-setuptools-scm-native \
	${PYTHON_PN}-setuptools-scm-git-archive-native \
"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-six \
	${PYTHON_PN}-compression \
"
