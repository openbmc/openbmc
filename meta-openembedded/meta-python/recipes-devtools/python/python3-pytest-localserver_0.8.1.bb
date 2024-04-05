SUMMARY = "pytest plugin to test server connections locally."
HOMEPAGE = "https://github.com/pytest-dev/pytest-localserver"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8da7a541d738b054dcbf70c31530432"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta ptest

SRC_URI[sha256sum] = "66569c34fef31a5750b16effd1cd1288a7a90b59155d005e7f916accd3dee4f1"

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-requests \
	python3-unittest-automake-output \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
	python3-werkzeug \
"
