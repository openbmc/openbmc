DESCRIPTION = "Fetch location and size of physical screens."
HOMEPAGE = "https://github.com/rr-/screeninfo"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a23813181e06852d377bc25ae5563a97"

# The PyPI package omits files for testing
# so use the GitHub source instead.
SRCREV = "0cf3055ccaf583a00a7a3a049f85a7c58dfd8884"
SRC_URI = " \
    git://github.com/rr-/screeninfo;branch=master;protocol=https \
    file://run-ptest \
"

inherit python_poetry_core ptest

RDEPENDS:${PN} += "\
    python3-core \
    python3-profile \
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
    cp -f ${S}/README.md ${D}${PTEST_PATH}/
}
