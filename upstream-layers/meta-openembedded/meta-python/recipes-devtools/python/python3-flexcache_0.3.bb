SUMMARY = "Saves and loads to the cache a transformed versions of a source object."
HOMEPAGE = "https://github.com/hgrecco/flexcache"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32f547dac365c355d2cdbcd7ebea9144"
DEPENDS += "python3-setuptools-scm-native"
SRC_URI[sha256sum] = "18743bd5a0621bfe2cf8d519e4c3bfdf57a269c15d1ced3fb4b64e0ff4600656"

inherit pypi python_setuptools_build_meta ptest-python-pytest
PYPI_PACKAGE = "flexcache"

PTEST_PYTEST_DIR = "flexcache/testsuite"

RDEPENDS:${PN} += " \
	python3-compression \
	python3-email \
	python3-json \
	python3-pickle \
	python3-typing-extensions \
"
