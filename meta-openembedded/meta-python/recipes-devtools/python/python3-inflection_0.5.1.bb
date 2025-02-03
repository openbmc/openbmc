SUMMARY = "A port of Ruby on Rails' inflection to Python."
HOMEPAGE = "https://pypi.org/project/inflection"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2fb6fa1a6f1792d78de19ad1bb653c31"

SRC_URI[sha256sum] = "1a29730d366e996aaacffb2f1f1cb9593dc38e2ddd30c91250c6dde09ea9b417"

inherit pypi setuptools3 ptest-python-pytest

do_install_ptest:append () {
	cp -f ${S}/test_inflection.py ${D}${PTEST_PATH}/
}


RDEPENDS:${PN} += "python3-pytest"

BBCLASSEXTEND = "native nativesdk"
