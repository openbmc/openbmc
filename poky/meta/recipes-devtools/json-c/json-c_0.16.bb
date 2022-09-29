SUMMARY = "C bindings for apps which will manipulate JSON data"
DESCRIPTION = "JSON-C implements a reference counting object model that allows you to easily construct JSON objects in C."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

SRC_URI = " \
    https://s3.amazonaws.com/json-c_releases/releases/${BP}.tar.gz \
    file://0001-Fix-build-with-clang-15.patch \
    file://run-ptest \
"
SRC_URI[sha256sum] = "8e45ac8f96ec7791eaf3bb7ee50e9c2100bbbc87b8d0f1d030c5ba8a0288d96b"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/tags"
UPSTREAM_CHECK_REGEX = "json-c-(?P<pver>\d+(\.\d+)+)-\d+"

RPROVIDES:${PN} = "libjson"

inherit cmake ptest

do_install_ptest() {
    install -d ${D}/${PTEST_PATH}/tests
    install ${B}/tests/test* ${D}/${PTEST_PATH}/tests
    install ${S}/tests/*.test ${D}/${PTEST_PATH}/tests
    install ${S}/tests/*.expected ${D}/${PTEST_PATH}/tests
    install ${S}/tests/test-defs.sh ${D}/${PTEST_PATH}/tests
    install ${S}/tests/valid*json ${D}/${PTEST_PATH}/tests
}

BBCLASSEXTEND = "native nativesdk"
