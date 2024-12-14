DESCRIPTION = "An implementation of RFC 7049 - Concise Binary Object Representation (CBOR)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a79e64179819c7ce293372c059f1dbd8"
DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "b682820677ee1dbba45f7da11898d2720f92e06be36acec290867d5ebf3d7e09"
SRC_URI += " \
        file://run-ptest \
"

inherit pypi python_setuptools_build_meta ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN}-ptest += " \
    python3-hypothesis \
    python3-pytest \
    python3-unittest-automake-output \
    python3-unixadmin \
"
RDEPENDS:${PN} += " \
    python3-datetime \
"

BBCLASSEXTEND = "native nativesdk"
