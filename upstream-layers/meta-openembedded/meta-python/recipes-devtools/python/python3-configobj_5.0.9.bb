SUMMARY = "Config file reading, writing and validation."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e2df3cb285297a24cd1097dfe6e96f95"

SRC_URI[sha256sum] = "03c881bbf23aa07bccf1b837005975993c4ab4427ba57f959afdd9d1a2386848"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "src/tests"

RDEPENDS:${PN} += " \
	python3-pprint \
	python3-six \
"
