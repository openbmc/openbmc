SUMMARY = "TOML for Modern C++"
DESCRIPTION = "toml11 is a feature-rich TOML language library for \
               C++11/14/17/20."

HOMEPAGE = "https://github.com/ToruNiina/toml11"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=44d1fcf70c7aa6991533c38daf7befa3"

PE = "1"

SRCREV = "cc0bee4fd46ea1f5db147d63ea545208cc9e8405"
SRCREV_json = "9cca280a4d0ccf0c08f47a99aa71d1b0e52f8d03"
SRCREV_doctest = "ae7a13539fb71f270b87eb2e874fbac80bc8dda2"

SRC_URI = "git://github.com/ToruNiina/toml11;branch=main;protocol=https \
           git://github.com/nlohmann/json;destsuffix=git/tests/extlib/json;name=json;branch=develop;protocol=https \
           git://github.com/doctest/doctest;destsuffix=git/tests/extlib/doctest;name=doctest;branch=master;protocol=https \
           file://0001-fix-add-missing-zero-initialization-to-region.patch \
           file://run-ptest \
"
SRCREV_FORMAT = "json_doctest"

S = "${WORKDIR}/git"

inherit cmake ptest

EXTRA_OECMAKE += "-DTOML11_PRECOMPILE=ON \
                  -DTOML11_BUILD_TESTS=${@bb.utils.contains("DISTRO_FEATURES", "ptest", "ON", "OFF", d)} \
"

ALLOW_EMPTY:${PN} = "1"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp -r ${B}/tests/test_* ${D}${PTEST_PATH}/tests
}
