SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=f969127d7b7ed0a8a63c2bbeae002588"

CVE_PRODUCT = "json-for-modern-cpp"

SRC_URI = "git://github.com/nlohmann/json.git;branch=develop;protocol=https \
           git://github.com/nlohmann/json_test_data.git;destsuffix=git/json_test_data;name=json-test-data;branch=master;protocol=https \
           file://run-ptest \
"

SRCREV = "9cca280a4d0ccf0c08f47a99aa71d1b0e52f8d03"
SRCREV_json-test-data = "a1375cea09d27cc1c4cadb8d00470375b421ac37"

SRCREV_FORMAT .= "_json-test-data"

S = "${WORKDIR}/git"

inherit cmake ptest

EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DJSON_BuildTests=ON -DJSON_TestDataDirectory=${PTEST_PATH}/json_test_data', '-DJSON_BuildTests=OFF', d)}"

# nlohmann-json is a header only C++ library, so the main package will be empty.
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN}-dev = ""
RDEPENDS:${PN}-ptest = "perl"

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
