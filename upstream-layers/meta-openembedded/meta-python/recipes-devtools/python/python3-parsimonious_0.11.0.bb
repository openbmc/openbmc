SUMMARY = "Parsimonious aims to be the fastest arbitrary-lookahead parser written in pure Python."
HOMEPAGE = "https://github.com/erikrose/parsimonious"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3396ea30f9d21389d7857719816f83b5"

SRC_URI[sha256sum] = "e080377d98957beec053580d38ae54fcdf7c470fb78670ba4bf8b5f9d5cad2a9"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "parsimonious/tests"

RDEPENDS:${PN} += "python3-regex"

# ModuleNotFoundError: No module named 'timeit'
RDEPENDS:${PN}-ptest += "python3-misc"
