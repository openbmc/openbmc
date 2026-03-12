SUMMARY = "LibYAML is a YAML 1.1 parser and emitter written in C."
DESCRIPTION = "LibYAML is a C library for parsing and emitting data in YAML 1.1, \
a human-readable data serialization format. "
HOMEPAGE = "https://pyyaml.org/wiki/LibYAML"
SECTION = "libs/devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://License;md5=7bbd28caa69f81f5cd5f48647236663d"

SRC_URI = "https://pyyaml.org/download/libyaml/yaml-${PV}.tar.gz \
           file://run-ptest \
"
SRC_URI[sha256sum] = "c642ae9b75fee120b2d96c712538bd2cf283228d2337df2cf2988e3c02678ef4"

S = "${UNPACKDIR}/yaml-${PV}"

inherit autotools ptest

DISABLE_STATIC:class-nativesdk = ""
DISABLE_STATIC:class-native = ""

BBCLASSEXTEND = "native nativesdk"

PTEST_TESTS = "test-version test-reader"

do_compile_ptest() {
    oe_runmake -C tests ${PTEST_TESTS}
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    for test in ${PTEST_TESTS}; do
        ${B}/libtool --mode=install install ${B}/tests/${test} ${D}${PTEST_PATH}/tests/
    done
}
