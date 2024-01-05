DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/ralphbean/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "GPL-3.0-only"

PYPI_PACKAGE = "ansi2html"

SRC_URI[sha256sum] = "5c6837a13ecc1903aab7a545353312049dfedfe5105362ad3a8d9d207871ec71"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-six \
	${PYTHON_PN}-compression \
"
