SUMMARY = "Resolve JSON Pointers in Python"
HOMEPAGE = "https://github.com/stefankoegl/python-json-pointer"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=32b15c843b7a329130f4e266a281ebb3"

inherit pypi ptest setuptools3

SRC_URI += "file://0001-Clean-up-test-runner.patch"

SRC_URI[sha256sum] = "97cba51526c829282218feb99dab1b1e6bdf8efd1c43dc9d57be093c0d69c99a"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-json \
"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-doctest \
	${PYTHON_PN}-unittest \
	${PYTHON_PN}-unittest-automake-output \
"

do_install_ptest() {
	cp -f ${S}/tests.py ${D}${PTEST_PATH}/
}
