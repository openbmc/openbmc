SUMMARY = "File transport adapter for Requests"
HOMEPAGE = "http://github.com/dashea/requests-file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9cc728d6087e43796227b0a31422de6b"

SRC_URI[md5sum] = "c96daf6b0c56687556e8a52748fd896c"
SRC_URI[sha256sum] = "07d74208d3389d01c38ab89ef403af0cfec63957d53a0081d8eca738d0247d8e"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS_${PN} += " \
    python3-requests \
"

