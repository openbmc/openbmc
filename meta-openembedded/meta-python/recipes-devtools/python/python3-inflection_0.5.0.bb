SUMMARY = "A port of Ruby on Rails' inflection to Python."
HOMEPAGE = "https://pypi.org/project/inflection"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2fb6fa1a6f1792d78de19ad1bb653c31"

SRC_URI[md5sum] = "87b2ab6ffdd6e15aa31d109ce5ff10bb"
SRC_URI[sha256sum] = "f576e85132d34f5bf7df5183c2c6f94cfb32e528f53065345cf71329ba0b8924"

inherit pypi setuptools3 ptest

SRC_URI +=" \
	file://run-ptest \
"

RDEPENDS_${PN}_ptest +=" \
	${PYTHON_PN}_pytest \
"

do_install_ptest() {
	cp -f ${S}/test_inflection.py ${D}${PTEST_PATH}/
}


RDEPENDS_${PN} += "${PYTHON_PN}-pytest"

BBCLASSEXTEND = "native nativesdk"
