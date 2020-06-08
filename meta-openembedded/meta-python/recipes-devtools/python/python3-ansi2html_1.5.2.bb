DESCRPTION = "ansi2html - Convert text with ANSI color codes to HTML or to LaTeX"
HOMEPAGE = "https://github.com/ralphbean/ansi2html"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
LICENSE = "GPLv3"

PYPI_PACKAGE = "ansi2html"

SRC_URI[md5sum] = "52d6085ad1c5970082ea5305a26af981"
SRC_URI[sha256sum] = "96ae85ae7b26b7da674d87de2870ba4d1964bca733ae4614587080b6358c3ba9"

inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-six"
