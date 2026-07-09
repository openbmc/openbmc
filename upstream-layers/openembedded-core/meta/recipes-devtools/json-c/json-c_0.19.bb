SUMMARY = "A JSON implementation in C"
DESCRIPTION = "JSON-C implements a reference counting object model that allows \
you to easily construct JSON objects in C, output them as JSON formatted strings \
and parse JSON formatted strings back into the C representation of JSON objects."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

SRC_URI = "https://s3.amazonaws.com/json-c_releases/releases/${BP}.tar.gz \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "37ad0249902e301bd9052bf712e511fcc6acff4ecaad4b5900aad9ce564e26de"

# NVD uses full tag name including date
CVE_VERSION = "0.19-20260627"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/tags"
UPSTREAM_CHECK_REGEX = "json-c-(?P<pver>\d+(\.\d+)+)-\d+"

RPROVIDES:${PN} = "libjson"

EXTRA_OECMAKE = "-DDISABLE_WERROR=ON"

inherit cmake ptest

do_install_ptest() {
    install -d ${D}/${PTEST_PATH}/tests
    install ${B}/tests/test* ${D}/${PTEST_PATH}/tests
    install ${S}/tests/*.test ${D}/${PTEST_PATH}/tests
    install ${S}/tests/*.expected ${D}/${PTEST_PATH}/tests
    install ${S}/tests/test-defs.sh ${D}/${PTEST_PATH}/tests
    install ${S}/tests/*json ${D}/${PTEST_PATH}/tests

    # The 'json_parse_cli' test executes the 'json_parse' app via
    # '../apps/json_parse' so we package it accordingly.
    #
    # Note that the test also depends on xxd, which is provided via busybox and
    # thus not listed as an explicit RDEPENDS.
    install -D -t ${D}/${PTEST_PATH}/apps/ ${B}/apps/json_parse
}

BBCLASSEXTEND = "native nativesdk"
