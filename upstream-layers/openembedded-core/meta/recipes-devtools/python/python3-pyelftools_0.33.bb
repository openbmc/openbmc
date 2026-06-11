SUMMARY = "pyelftools is a pure-Python library for parsing and analyzing ELF files and DWARF debugging information"
HOMEPAGE = "https://github.com/eliben/pyelftools"
SECTION = "devel/python"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ce2a2b07fca326bc7c146d10105ccfc"

SRC_URI[sha256sum] = "660d82dcbeb8e83d1702bd97f223f761625da06111c0cc988eac6b8ab0c1b61f"

PYPI_PACKAGE = "pyelftools"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "python3-debugger python3-pprint python3-logging"
