SUMMARY = "Lightweight and flexible command-line JSON processor"
DESCRIPTION = "jq is like sed for JSON data, you can use it to slice and \
               filter and map and transform structured data with the same \
               ease that sed, awk, grep and friends let you play with text."
HOMEPAGE = "https://jqlang.github.io/jq/"
BUGTRACKER = "https://github.com/jqlang/jq/issues"
SECTION = "utils"
LICENSE = "MIT & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf7fcb0a1def4a7ad62c028f7d0dca47"

SRCREV = "4467af7068b1bcd7f882defff6e7ea674c5357f4"

SRC_URI = " \
    git://github.com/jqlang/jq.git;protocol=https;branch=master;tag=jq-${PV} \
    file://run-ptest \
    file://0001-Support-building-with-disable-maintainer-mode-and-so.patch \
"

inherit autotools ptest

UPSTREAM_CHECK_GITTAGREGEX = "${BPN}-(?P<pver>\d+(\.\d+)+)"

PACKAGECONFIG ?= "oniguruma"

PACKAGECONFIG[docs] = "--enable-docs,--disable-docs,ruby-native"
PACKAGECONFIG[maintainer-mode] = "--enable-maintainer-mode,--disable-maintainer-mode,flex-native bison-native"
PACKAGECONFIG[oniguruma] = "--with-oniguruma,--without-oniguruma,onig"
# enable if you want ptest running under valgrind
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind"

do_configure:append() {
	sed -i -e "/^ac_cs_config=/ s:${WORKDIR}::g" ${B}/config.status
}

do_install_ptest() {
    cp -rf ${S}/tests ${D}${PTEST_PATH}
    cp -rf ${B}/.libs ${D}${PTEST_PATH}
    # libjq.so.* is packaged in the main jq component, so remove it from ptest
    rm -f ${D}${PTEST_PATH}/.libs/libjq.so.*
    ln -sf ${bindir}/jq ${D}${PTEST_PATH}
    if [ "${@bb.utils.contains('PACKAGECONFIG', 'valgrind', 'true', 'false', d)}" = "false" ]; then
        sed -i 's:#export NO_VALGRIND=1:export NO_VALGRIND=1:g' ${D}${PTEST_PATH}/run-ptest
    fi
    # handle multilib
    sed -i s:@libdir@:${libdir}:g ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS:${PN}-ptest += "tzdata"

BBCLASSEXTEND = "native"
