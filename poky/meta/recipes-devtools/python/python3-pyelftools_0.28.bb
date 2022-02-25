DESCRIPTION = "pyelftools is a pure-Python library for parsing and analyzing ELF files and DWARF debugging information"
HOMEPAGE = "https://github.com/eliben/pyelftools"
SECTION = "devel/python"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ce2a2b07fca326bc7c146d10105ccfc"

SRC_URI[sha256sum] = "53e5609cac016471d40bd88dc410cd90755942c25e58a61021cfdf7abdfeacff"

PYPI_PACKAGE = "pyelftools"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "${PYTHON_PN}-debugger ${PYTHON_PN}-pprint"
