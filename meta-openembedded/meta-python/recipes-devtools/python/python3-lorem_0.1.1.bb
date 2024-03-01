DESCRIPTION = "Generator for random text that looks like Latin"
HOMEPAGE = "https://github.com/sfischer13/python-lorem"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5301c01b2bcdeedba23645f94db2f179"
SRC_URI[md5sum] = "e3f0064a94c13e19780eb724affdb426"
SRC_URI[sha256sum] = "785f4109a241fc2891e59705e85d065f6e6d3ed6ad91750a8cb54d4f3e59d934"

SRC_URI += "\
        file://run-ptest \
"

PYPI_PACKAGE = "lorem"

inherit pypi setuptools3 ptest

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        python3 \
"
RDEPENDS:${PN}-ptest += " \
        python3-pytest \
        python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
