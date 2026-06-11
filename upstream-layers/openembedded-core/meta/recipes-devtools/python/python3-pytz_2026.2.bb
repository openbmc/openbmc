SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = "http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=1a67fc46c1b596cce5d21209bbe75999"

inherit pypi setuptools3 ptest-python-pytest

PTEST_PYTEST_DIR = "pytz/tests"

SRC_URI[sha256sum] = "0e60b47b29f21574376f218fe21abc009894a2321ea16c6754f3cad6eb7cdd6a"

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
