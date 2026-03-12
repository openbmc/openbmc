SUMMARY = "YANG data modeling language library"
DESCRIPTION = "libyang is a YANG data modelling language parser and toolkit written (and providing API) in C."
HOMEPAGE = "https://github.com/CESNET/libyang"
SECTION = "libs"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b69fd3b2815bbf1cef5c97f0eee2519a"

SRCREV = "c2ddd01b9b810a30d6a7d6749a3bc9adeb7b01fb"

SRC_URI = "git://github.com/CESNET/libyang.git;branch=master;protocol=https;tag=v${PV} \
           file://0001-test_context-skip-test-case-test_searchdirs.patch \
           file://run-ptest \
           "


# Main dependencies
inherit cmake pkgconfig lib_package ptest multilib_header
DEPENDS = " \
    libpcre2 \
    xxhash \
"
DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'cmocka', '', d)}"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += " ${@bb.utils.contains('PTEST_ENABLED', '1', '-DENABLE_TESTS=ON -DENABLE_VALGRIND_TESTS=OFF', '', d)}"

do_compile:prepend () {
    if [ ${PTEST_ENABLED} = "1" ]; then
        sed -i -e 's|${S}|${PTEST_PATH}|g' ${B}/tests/tests_config.h
        sed -i -e 's|${B}|${PTEST_PATH}|g' ${B}/tests/tests_config.h
    fi
}

do_install:append () {
        oe_multilib_header  libyang/ly_config.h
}

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp -f ${B}/tests/utest_* ${D}${PTEST_PATH}/tests/
    cp -fR ${S}/tests/modules ${D}${PTEST_PATH}/tests/
    install -d ${D}${PTEST_PATH}/tests/plugins
    cp -f ${B}/tests/plugins/plugin_*.so ${D}${PTEST_PATH}/tests/plugins/
}

FILES:${PN} += "${datadir}/yang/*"
