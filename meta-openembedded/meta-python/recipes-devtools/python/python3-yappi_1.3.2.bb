SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "https://github.com/sumerc/yappi"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71c208c9a4fd864385eb69ad4caa3bee"

SRC_URI[sha256sum] = "a51d3e6e5563cc74b5bb82ed6e7bd44a9c1a7eae3d97e4d52e9465edb3a8da8d"

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
