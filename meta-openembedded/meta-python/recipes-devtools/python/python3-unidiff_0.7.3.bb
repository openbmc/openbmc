SUMMARY = "Unified diff parsing/metadata extraction library"
HOMEPAGE = "http://github.com/matiasb/python-unidiff"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4c434b08ef42fea235bb019b5e5a97b3"

SRC_URI[sha256sum] = "d5f2e53a9a00db3224a8c36349b5380e0e22d1aec6c694b14fb9483ee93c6205"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
       ${PYTHON_PN}-pytest \
"

do_install_ptest() {
      install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-io \
"
