DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/pycontribs/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "LGPL-3.0-or-later"


SRC_URI[sha256sum] = "ae17e92f1d6cac0e67d367e07fb0ba8a23dac8f193fc5d7a8c8a50fe5ddd95a4"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	python3-setuptools-scm-native \
"

RDEPENDS:${PN} = " \
	python3-compression \
"
