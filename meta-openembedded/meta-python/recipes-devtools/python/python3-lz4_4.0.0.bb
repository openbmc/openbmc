DESCRIPTION = "python bindings for the lz4 compression library by Yann Collet"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6231efa4dd4811e62407314d90a57573"

DEPENDS += " \
    lz4 \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-pkgconfig-native \
"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "57c5dfd3b7dae833b0d2b2c1aafd7f9d0dfcab40683d183d010c67c9fd1beca3"

inherit pkgconfig pypi python_setuptools_build_meta ptest

RDEPENDS:${PN}-ptest += "${PYTHON_PN}-pytest ${PYTHON_PN}-multiprocessing ${PYTHON_PN}-psutil"

do_install_ptest() {
    cp -rf ${S}/tests/ ${D}${PTEST_PATH}/
    # The stream API is experimental and not enabled yet, so don't ship the test suite
    rm -rf ${D}${PTEST_PATH}/tests/stream
}

BBCLASSEXTEND = "native nativesdk"
