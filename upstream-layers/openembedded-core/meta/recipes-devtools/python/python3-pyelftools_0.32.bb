SUMMARY = "pyelftools is a pure-Python library for parsing and analyzing ELF files and DWARF debugging information"
HOMEPAGE = "https://github.com/eliben/pyelftools"
SECTION = "devel/python"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ce2a2b07fca326bc7c146d10105ccfc"

SRC_URI[sha256sum] = "6de90ee7b8263e740c8715a925382d4099b354f29ac48ea40d840cf7aa14ace5"

PYPI_PACKAGE = "pyelftools"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "python3-debugger python3-pprint python3-logging"
