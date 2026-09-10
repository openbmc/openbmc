DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/pycontribs/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "LGPL-3.0-or-later"


SRC_URI[sha256sum] = "190b3fc65f0545fec6442527f8f55131949410fe97c15ac42a441f1e96726f57"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	python3-setuptools-scm-native \
"

RDEPENDS:${PN} = " \
	python3-compression \
"
