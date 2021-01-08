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

SRC_URI[sha256sum] = "4b86adb3ed913c1ef14a276301981f696ab4ec360c19f0a5b68235c4756abae5"

UPSTREAM_CHECK_URI = "https://www.kernel.org/pub/software/network/ethtool/"

inherit autotools ptest bash-completion pkgconfig

RDEPENDS_${PN}-ptest += "make"

PACKAGECONFIG ?= "netlink"
PACKAGECONFIG[netlink] = "--enable-netlink,--disable-netlink,libmnl,"

do_compile_ptest() {
   oe_runmake buildtest-TESTS
}

do_install_ptest () {
   cp ${B}/Makefile                 ${D}${PTEST_PATH}
   install ${B}/test-cmdline        ${D}${PTEST_PATH}
   if ${@bb.utils.contains('PACKAGECONFIG', 'netlink', 'false', 'true', d)}; then
       install ${B}/test-features       ${D}${PTEST_PATH}
   fi
   install ${B}/ethtool             ${D}${PTEST_PATH}/ethtool
   sed -i 's/^Makefile/_Makefile/'  ${D}${PTEST_PATH}/Makefile
}
