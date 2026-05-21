SUMMARY = "Google Authentication Library"
DESCRIPTION = "This library provides oauthlib integration with google-auth"
HOMEPAGE = "https://github.com/googleapis/google-auth-library-python-oauthlib"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI += "file://0001-python3-google-auth-oauthlib-Skip-failing-3PI-creden.patch"
SRC_URI[sha256sum] = "18b5e28880eb8eba9065c436becdc0ee8e4b59117a73a510679c82f70cd363d2"

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
