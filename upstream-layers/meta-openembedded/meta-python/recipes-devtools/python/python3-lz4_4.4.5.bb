DESCRIPTION = "python bindings for the lz4 compression library by Yann Collet"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6231efa4dd4811e62407314d90a57573"

DEPENDS += " \
    lz4 \
    python3-setuptools-scm-native \
    python3-pkgconfig-native \
"

SRC_URI[sha256sum] = "5f0b9e53c1e82e88c10d7c180069363980136b9d7a8306c4dca4f760d60c39f0"

inherit pkgconfig pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}-ptest += "\
    python3-coverage \
    python3-pytest-cov \
    python3-pytest-runner \
    python3-multiprocessing \
    python3-psutil \
"

do_install_ptest:append () {
    # The stream API is experimental and not enabled yet, so don't ship the test suite
    rm -rf ${D}${PTEST_PATH}/tests/stream
}

BBCLASSEXTEND = "native nativesdk"
