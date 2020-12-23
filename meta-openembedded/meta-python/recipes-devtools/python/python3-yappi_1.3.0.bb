SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "https://github.com/sumerc/yappi"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71c208c9a4fd864385eb69ad4caa3bee"

SRC_URI[md5sum] = "6dde41b116566cbe1575e41a1e376f74"
SRC_URI[sha256sum] = "a443240f4a776fa1be04430bf423dbf09615c05eba34f4a2a6af344a7ce8ff61"

SRC_URI += " \
    file://run-ptest \
"

inherit pypi setuptools3 ptest

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
"

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-profile \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    cp -f ${S}/yappi/yappi.py ${D}/${PTEST_PATH}/
}
