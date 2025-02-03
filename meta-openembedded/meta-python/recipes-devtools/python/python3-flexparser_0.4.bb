SUMMARY = "Parsing made fun ... using typing."
HOMEPAGE = "https://github.com/hgrecco/flexparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32f547dac365c355d2cdbcd7ebea9144"

DEPENDS += "python3-setuptools-scm-native"
SRC_URI[sha256sum] = "266d98905595be2ccc5da964fe0a2c3526fbbffdc45b65b3146d75db992ef6b2"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PYPI_PACKAGE = "flexparser"

PTEST_PYTEST_DIR = "flexparser/testsuite"

RDEPENDS:${PN} += " \
	python3-compression \
	python3-email \
	python3-logging \
"
