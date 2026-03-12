SUMMARY = "Liblognorm is a fast-samples based normalization library."
DESCRIPTION = "Briefly described, liblognorm is a tool to normalize log data."
HOMEPAGE = "http://www.liblognorm.com"
SECTION = "base"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=ca016db57e008528dace002188c73dad"

DEPENDS = "libfastjson libestr"

SRC_URI = "git://github.com/rsyslog/liblognorm;branch=stable;protocol=https;tag=v${PV} \
           file://0001-Add-asprintf-to-autoconf-function-check-macro.patch \
           "
SRCREV = "3804548e3541747bde3e82a78d2c2dcd865296dd"

inherit autotools pkgconfig

PACKAGECONFIG ??= "testbench tools"
PACKAGECONFIG[regexp] = "--enable-regexp,--disable-regexp,pcre2,"
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug,,"
PACKAGECONFIG[advstats] = "--enable-advanced-stats,--disable-advanced-stats,,"
PACKAGECONFIG[docs] = "--enable-docs,--disable-docs,sphinx,"
PACKAGECONFIG[testbench] = "--enable-testbench,--disable-testbench,,"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,,"
PACKAGECONFIG[tools] = "--enable-tools,--disable-tools,,"
