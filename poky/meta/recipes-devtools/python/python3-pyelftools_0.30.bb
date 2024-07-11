SUMMARY = "pyelftools is a pure-Python library for parsing and analyzing ELF files and DWARF debugging information"
HOMEPAGE = "https://github.com/eliben/pyelftools"
SECTION = "devel/python"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ce2a2b07fca326bc7c146d10105ccfc"

SRC_URI[sha256sum] = "2fc92b0d534f8b081f58c7c370967379123d8e00984deb53c209364efd575b40"

PYPI_PACKAGE = "pyelftools"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "python3-debugger python3-pprint"
