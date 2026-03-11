SUMMARY = "Google Authentication Library"
DESCRIPTION = "This library provides oauthlib integration with google-auth"
HOMEPAGE = "https://github.com/googleapis/google-auth-library-python-oauthlib"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "292d2d3783349f2b0734a0a0207b1e1e322ac193c2c09d8f7c613fb7cc501ea8"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
        python3-click \
        python3-mock \
        python3-pytest \
        python3-unittest-automake-output \
        python3-unixadmin \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
        python3-google-auth \
        python3-requests-oauthlib \
"
