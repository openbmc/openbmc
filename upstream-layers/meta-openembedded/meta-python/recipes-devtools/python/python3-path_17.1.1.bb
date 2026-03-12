SUMMARY = "A module wrapper for os.path"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1aeae65f25a15b1e46d4381f2f094e0a"

SRC_URI[sha256sum] = "2dfcbfec8b4d960f3469c52acf133113c2a8bf12ac7b98d629fa91af87248d42"

SRC_URI += "\
    file://run-ptest \
"

inherit pypi python_setuptools_build_meta ptest

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-coherent-licensed-native \
"

RDEPENDS:${PN} += " \
    python3-appdirs \
    python3-crypt \
    python3-io \
    python3-numbers \
    python3-shell \
"
RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
    python3-more-itertools \
"

BBCLASSEXTEND = "nativesdk native"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    cp -rf ${S}/tests/test_* ${D}${PTEST_PATH}/test/
    install -d ${D}${PTEST_PATH}/path
    cp -rf ${S}/path/* ${D}${PTEST_PATH}/path/
}
