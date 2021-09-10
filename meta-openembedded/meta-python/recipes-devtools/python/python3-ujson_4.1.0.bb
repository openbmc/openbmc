SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=10fa10456417c0252bcb8a498e498ffe"

SRC_URI[sha256sum] = "22b63ec4409f0d2f2c4c9d5aa331997e02470b7a15a3233f3cc32f2f9b92d58c"

inherit pypi ptest setuptools3

SRC_URI += " \
    file://run-ptest \
"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-pytz \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
