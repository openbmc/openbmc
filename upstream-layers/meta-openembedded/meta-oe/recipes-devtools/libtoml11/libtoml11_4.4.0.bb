SUMMARY = "TOML for Modern C++"
DESCRIPTION = "toml11 is a feature-rich TOML language library for \
               C++11/14/17/20."

HOMEPAGE = "https://github.com/ToruNiina/toml11"
BUGTRACKER = "https://github.com/ToruNiina/toml11/issues"
CVE_PRODUCT = "libtoml11"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=44d1fcf70c7aa6991533c38daf7befa3"

PE = "1"

SRC_URI = "\
    gitsm://github.com/ToruNiina/toml11.git;protocol=https;branch=main;tag=v${PV} \
    file://0001-Remove-whitespace-in-operator.patch \
    file://run-ptest \
"
SRCREV = "be08ba2be2a964edcdb3d3e3ea8d100abc26f286"

inherit cmake ptest

EXTRA_OECMAKE += "-DTOML11_PRECOMPILE=ON \
                  -DTOML11_BUILD_TESTS=${@bb.utils.contains("PTEST_ENABLED", "1", "ON", "OFF", d)} \
"

ALLOW_EMPTY:${PN} = "1"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp -r ${B}/tests/test_* ${D}${PTEST_PATH}/tests
}

BBCLASSEXTEND = "native nativesdk"
