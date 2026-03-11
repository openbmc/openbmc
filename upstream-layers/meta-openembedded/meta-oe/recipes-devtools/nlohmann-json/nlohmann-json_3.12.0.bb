SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=3b489645de9825cca5beeb9a7e18b6eb"

CVE_PRODUCT = "json-for-modern-cpp"

SRC_URI = "git://github.com/nlohmann/json.git;branch=master;protocol=https \
           git://github.com/nlohmann/json_test_data.git;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/json_test_data;name=json-test-data;branch=master;protocol=https \
           file://run-ptest \
"

SRCREV = "55f93686c01528224f448c19128836e7df245f72"
SRCREV_json-test-data = "a1375cea09d27cc1c4cadb8d00470375b421ac37"

SRCREV_FORMAT .= "_json-test-data"


inherit cmake ptest

EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DJSON_BuildTests=ON -DJSON_TestDataDirectory=${PTEST_PATH}/json_test_data', '-DJSON_BuildTests=OFF', d)}"

# nlohmann-json is a header only C++ library, so the main package will be empty.
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN}-dev = ""
RDEPENDS:${PN}-ptest = "perl locale-base-de-de"

BBCLASSEXTEND = "native nativesdk"


do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp -r ${S}/json_test_data/ ${D}${PTEST_PATH}/
    cp -r ${B}/tests/test-* ${D}${PTEST_PATH}/tests
    rm -rf ${D}${PTEST_PATH}/json_test_data/.git
}


# other packages commonly reference the file directly as "json.hpp"
# create symlink to allow this usage
do_install:append() {
    ln -s nlohmann/json.hpp ${D}${includedir}/json.hpp
}
