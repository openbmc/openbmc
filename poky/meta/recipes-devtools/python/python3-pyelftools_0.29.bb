DESCRIPTION = "pyelftools is a pure-Python library for parsing and analyzing ELF files and DWARF debugging information"
HOMEPAGE = "https://github.com/eliben/pyelftools"
SECTION = "devel/python"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ce2a2b07fca326bc7c146d10105ccfc"

SRC_URI[sha256sum] = "ec761596aafa16e282a31de188737e5485552469ac63b60cfcccf22263fd24ff"

PYPI_PACKAGE = "pyelftools"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "${PYTHON_PN}-debugger ${PYTHON_PN}-pprint"
