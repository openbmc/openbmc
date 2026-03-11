DESCRIPTION = "Interval arithmetic for Python"
HOMEPAGE = "https://github.com/AlexandreDecan/python-intervals"
SECTION = "devel/python"

LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

PYPI_PACKAGE := "python-intervals"

inherit pypi setuptools3 ptest-python-pytest

do_install_ptest:append () {
	cp -f ${S}/test_intervals.py ${D}${PTEST_PATH}
	cp -f ${S}/README.md ${D}${PTEST_PATH}
}

SRC_URI[sha256sum] = "0d26746eaed0be78a61dd289bb7a10721b08770bb3e807614835f490d514f2a5"

BBCLASSEXTEND = "native"
