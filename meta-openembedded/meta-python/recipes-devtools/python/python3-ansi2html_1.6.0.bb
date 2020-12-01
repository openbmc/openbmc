DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/ralphbean/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "GPLv3"

PYPI_PACKAGE = "ansi2html"

SRC_URI[sha256sum] = "0f124ea7efcf3f24f1f9398e527e688c9ae6eab26b0b84e1299ef7f94d92c596"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native ${PYTHON_PN}-toml-native"
RDEPENDS_${PN} = "${PYTHON_PN}-six"
