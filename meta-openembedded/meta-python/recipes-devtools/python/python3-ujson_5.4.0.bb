SUMMARY  = "Ultra fast JSON encoder and decoder for Python"
DESCRIPTION = "UltraJSON is an ultra fast JSON encoder and decoder written in pure C with bindings for Python 2.5+ and 3."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e0039a83d8a99726b5418f0b03302d0a"

SRC_URI[sha256sum] = "6b953e09441e307504130755e5bd6b15850178d591f66292bba4608c4f7f9b00"

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
