SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "https://github.com/sumerc/yappi"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71c208c9a4fd864385eb69ad4caa3bee"

SRC_URI[sha256sum] = "0a73c608a2603570a020a32d4369ba744012bc5267f37e5bd8026fb491abba56"

SRC_URI += " \
    file://run-ptest \
    file://0001-Fix-imports-for-ptests.patch \
"

inherit pypi setuptools3 ptest

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-gevent \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-profile \
    ${PYTHON_PN}-zopeinterface \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    cp -f ${S}/yappi/yappi.py ${D}/${PTEST_PATH}/
}

SKIP_RECIPE[python3-yappi] ?= "Not compatible with py3.11; needs a new release"
