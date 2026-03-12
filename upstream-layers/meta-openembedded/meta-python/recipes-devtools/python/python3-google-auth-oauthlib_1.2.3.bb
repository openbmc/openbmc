SUMMARY = "Google Authentication Library"
DESCRIPTION = "This library provides oauthlib integration with google-auth"
HOMEPAGE = "https://github.com/googleapis/google-auth-library-python-oauthlib"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "file://0001-python3-google-auth-oauthlib-Skip-failing-3PI-creden.patch"
SRC_URI[sha256sum] = "eb09e450d3cc789ecbc2b3529cb94a713673fd5f7a22c718ad91cf75aedc2ea4"

inherit pypi setuptools3 ptest
PYPI_PACKAGE = "google_auth_oauthlib"

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
