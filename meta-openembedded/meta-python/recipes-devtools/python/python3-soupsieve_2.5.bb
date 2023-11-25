SUMMARY = "CSS selector library for python-beautifulsoup4"
HOMEPAGE = "https://github.com/facelessuser/soupsieve"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=520586fa71ed2cbda50b4a8c89621e09"

SRC_URI[sha256sum] = "5663d5a7b3bfaeee0bc4372e7fc48f9cff4940b3eec54a6451cc5299f1097690"

inherit pypi python_hatchling python_setuptools_build_meta ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
        ${PYTHON_PN}-pytest \
        ${PYTHON_PN}-beautifulsoup4 \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
