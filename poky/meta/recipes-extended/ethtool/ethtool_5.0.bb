SUMMARY = "Display or change ethernet card settings"
DESCRIPTION = "A small utility for examining and tuning the settings of your ethernet-based network interfaces."
HOMEPAGE = "http://www.kernel.org/pub/software/network/ethtool/"
SECTION = "console/network"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://ethtool.c;beginline=4;endline=17;md5=c19b30548c582577fc6b443626fc1216"

SRC_URI = "${KERNELORG_MIRROR}/software/network/ethtool/ethtool-${PV}.tar.gz \
           file://run-ptest \
           file://avoid_parallel_tests.patch \
           "

SRC_URI[md5sum] = "8998c9eb7e491b0aec420a807ce52ba6"
SRC_URI[sha256sum] = "cc53a6d4d5643f8993ef20d6b638f88d9035529a9e777e222073c3a5b9237178"

inherit autotools ptest
RDEPENDS_${PN}-ptest += "make"

do_compile_ptest() {
   oe_runmake buildtest-TESTS
}

do_install_ptest () {
   cp ${B}/Makefile                 ${D}${PTEST_PATH}
   install ${B}/test-cmdline        ${D}${PTEST_PATH}
   install ${B}/test-features       ${D}${PTEST_PATH}
   install ${B}/ethtool             ${D}${PTEST_PATH}/ethtool
   sed -i 's/^Makefile/_Makefile/'  ${D}${PTEST_PATH}/Makefile
}
