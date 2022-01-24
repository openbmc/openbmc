SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=10fa10456417c0252bcb8a498e498ffe"

SRC_URI[sha256sum] = "a88944d2f99db71a3ca0c63d81f37e55b660edde0b07216fb65a3e46403ef004"

inherit pypi ptest setuptools3

SRC_URI += " \
    file://run-ptest \
    file://0001-setup.py-Do-not-strip-debugging-symbols.patch \
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
