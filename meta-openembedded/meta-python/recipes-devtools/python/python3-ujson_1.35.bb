SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=88df8e78b9edfd744953862179f2d14e"

SRC_URI[md5sum] = "42f77b0cce686dfa4da2e68480b1dd24"
SRC_URI[sha256sum] = "f66073e5506e91d204ab0c614a148d5aa938bdbf104751be66f8ad7a222f5f86"

inherit pypi ptest setuptools3

SRC_URI += " \
    file://run-ptest \
"

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
