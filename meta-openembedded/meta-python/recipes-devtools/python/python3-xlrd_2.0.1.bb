SUMMARY = "Library for developers to extract data from Microsoft Excel (tm) spreadsheet files"
DESCRIPTION = "Extract data from Excel spreadsheets (.xls and .xlsx,\
 versions 2.0 onwards) on any platform. Pure Python (2.6, 2.7, 3.2+). \
Strong support for Excel dates. Unicode-aware."
HOMEPAGE = "http://www.python-excel.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00ea1e843a43c20d9b63a8112239b0d1"

SRC_URI[sha256sum] = "f72f148f54442c6b056bf931dbc34f986fd0c3b0b6b5a58d013c9aef274d0c88"


SRC_URI = "git://github.com/python-excel/xlrd.git;branch=master;protocol=https \
           file://run-ptest \
"
SRCREV = "b8d573e11ec149da695d695c81a156232b89a949"

S = "${WORKDIR}/git"

inherit ptest setuptools3
PIP_INSTALL_PACKAGE = "xlrd"
PIP_INSTALL_DIST_PATH = "${S}/dist"

RDEPENDS:${PN} += " \
    python3-compression \
    python3-io \
    python3-mmap \
    python3-pprint \
    python3-shell \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
