SUMMARY = "YANG data modeling language library"
DESCRIPTION = "libyang is a YANG data modelling language parser and toolkit written (and providing API) in C."
HOMEPAGE = "https://github.com/CESNET/libyang"
SECTION = "libs"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f3916d7d8d42a6508d0ea418cfff10ad"

SRCREV = "69d9fff65abb58beb0bb6aa9ecacd572ca1dfc56"

SRC_URI = "git://github.com/CESNET/libyang.git;branch=master;protocol=https \
           file://libyang-skip-pcre2-config-add-stdint-h.patch \
           file://run-ptest \
           "

S = "${WORKDIR}/git"

COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:armv5 = "null"
COMPATIBLE_HOST:riscv64 = "null"

# Main dependencies
inherit cmake pkgconfig lib_package binconfig-disabled ptest
DEPENDS = "libpcre2"
DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'cmocka', '', d)}"
BINCONFIG = "${bindir}/pcre2-config"

# Ptest dependencies
RDEPENDS:${PN}-ptest += "valgrind"

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += " ${@bb.utils.contains('PTEST_ENABLED', '1', '-DENABLE_BUILD_TESTS=ON', '', d)}"

do_install_ptest () {
    cp -fR ${B}/tests/ ${D}${PTEST_PATH}/
}

