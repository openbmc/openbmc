SUMMARY = "Python package to parse, read and write Microsoft OLE2 files"
HOMEPAGE = "https://github.com/decalage2/olefile"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f18814bd70fd28e11a4545145dcb3822"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "599383381a0bf3dfbd932ca0ca6515acd174ed48870cbf7fee123d698c192c1c"

inherit pypi setuptools3 ptest

PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN} += "\
    python3-core \
    python3-datetime \
    python3-logging \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
