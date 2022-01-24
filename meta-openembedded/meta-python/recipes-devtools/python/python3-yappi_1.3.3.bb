SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "https://github.com/sumerc/yappi"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71c208c9a4fd864385eb69ad4caa3bee"

SRC_URI[sha256sum] = "855890cd9a90d833dd2df632d648de8ccd0a4c3131f1edc8abd004db0625b5e8"

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
