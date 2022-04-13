SUMMARY = "YANG data modeling language library"
DESCRIPTION = "libyang is a YANG data modelling language parser and toolkit written (and providing API) in C."
HOMEPAGE = "https://github.com/CESNET/libyang"
SECTION = "libs"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f3916d7d8d42a6508d0ea418cfff10ad"

SRCREV = "a0cc89516ab5eca84d01c85309f320a94752a64c"

SRC_URI = "git://github.com/CESNET/libyang.git;branch=master;protocol=https \
           file://libyang-add-stdint-h.patch \
           file://run-ptest \
           "

S = "${WORKDIR}/git"

# Due to valgrind not supported on these arches:
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:armv5 = "null"
COMPATIBLE_HOST:riscv64 = "null"

# Main dependencies
inherit cmake pkgconfig lib_package ptest
DEPENDS = "libpcre2"
DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'cmocka', '', d)}"

# Ptest dependencies
RDEPENDS:${PN}-ptest += "valgrind"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += " ${@bb.utils.contains('PTEST_ENABLED', '1', '-DENABLE_TESTS=ON', '', d)}"

do_install_ptest () {
    cp -fR ${B}/tests/ ${D}${PTEST_PATH}/
}

