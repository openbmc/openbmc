DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/pycontribs/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "LGPL-3.0-or-later"

PYPI_PACKAGE = "ansi2html"

SRC_URI[sha256sum] = "3453bf87535d37b827b05245faaa756dbab4ec3d69925e352b6319c3c955c0a5"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	python3-setuptools-scm-native \
"

RDEPENDS:${PN} = " \
	python3-six \
	python3-compression \
"
