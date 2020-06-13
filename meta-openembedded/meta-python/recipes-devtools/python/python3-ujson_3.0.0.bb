SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=10fa10456417c0252bcb8a498e498ffe"

SRC_URI[md5sum] = "1c13a485776a2a0dfa1795d101bb3d57"
SRC_URI[sha256sum] = "e0199849d61cc6418f94d52a314c6a27524d65e82174d2a043fb718f73d1520d"

inherit pypi ptest setuptools3

SRC_URI += " \
    file://run-ptest \
"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
"

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytz \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
