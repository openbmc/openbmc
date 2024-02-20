DESCRIPTION = "cmocka is an elegant unit testing framework for C with support for mock \
objects. It only requires the standard C library, works on a range of computing \
platforms (including embedded) and with different compilers."
HOMEPAGE = "https://cmocka.org/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "a01cc69ee9536f90e57c61a198f2d1944d3d4313"
PV .= "+git"
SRC_URI = "git://git.cryptomilk.org/projects/cmocka.git;protocol=https;branch=stable-1.1 \
           file://0001-include-Check-for-previous-declaration-of-uintptr_t.patch \
           file://run-ptest \
          "

S = "${WORKDIR}/git"

inherit cmake ptest

EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DCMAKE_BUILD_TYPE=Debug -DUNIT_TESTING=ON', '', d)}"
# Use -Wl,wrap linker flag, which does not work with LTO
LTO = ""

do_install:append () {
    install -d ${D}${libdir}/${BPN}/example
    install -d ${D}${libdir}/${BPN}/example/mock/chef_wrap
    install -d ${D}${libdir}/${BPN}/example/mock/uptime

    install -m 0755 ${B}/example/*_test ${D}/${libdir}/${BPN}/example
    install -m 0755 ${B}/example/mock/chef_wrap/waiter_test_wrap ${D}/${libdir}/${BPN}/example/mock/chef_wrap
    install -m 0755 ${B}/example/mock/uptime/uptime ${D}/${libdir}/${BPN}/example/mock/uptime
    install -m 0755 ${B}/example/mock/uptime/test_uptime ${D}/${libdir}/${BPN}/example/mock/uptime
    install -m 0644 ${B}/example/mock/uptime/libproc_uptime.so ${D}/${libdir}/${BPN}/example/mock/libproc_uptime.so
}

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/tests/test_* ${D}${PTEST_PATH}/tests
}

PACKAGE_BEFORE_PN += "${PN}-examples"

FILES:${PN}-examples = "${libdir}/${BPN}/example"
