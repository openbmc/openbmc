SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = "http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1a67fc46c1b596cce5d21209bbe75999"

inherit pypi setuptools3 ptest-python-pytest

PTEST_PYTEST_DIR = "pytz/tests"

SRC_URI[sha256sum] = "2aa355083c50a0f93fa581709deac0c9ad65cca8a9e9beac660adcbd493c798a"

RDEPENDS:${PN}:class-target += "\
    python3-datetime \
    python3-doctest \
    python3-io \
    python3-pickle \
    python3-pprint \
    python3-threading \
"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest:append() {
	cp -f ${S}/README.rst ${D}${PTEST_PATH}/
}
