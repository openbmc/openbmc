SUMMARY = "NCurses Disk Usage"
DESCRIPTION = "\
    ncdu is a curses-based version of the well-known 'du', and provides a \
    fast way to see what directories are using your disk space. \
"
HOMEPAGE = "https://dev.yorhel.nl/ncdu"
BUGTRACKER = "https://code.blicky.net/yorhel/ncdu/issues"
SECTION = "console/utils"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fd36a36514d73885d04105f000da9813"

DEPENDS += "ncurses"

SRC_URI += "git://code.blicky.net/yorhel/ncdu.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "7a1437389691c4adf1999afce63850c808602f08"

inherit autotools pkgconfig

PACKAGECONFIG ?= "largefile ncursesw year2038"

PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile"
PACKAGECONFIG[ncurses] = "--with-ncurses,--without-ncurses"
PACKAGECONFIG[ncursesw] = "--with-ncursesw,--without-ncursesw"
PACKAGECONFIG[year2038] = "--enable-year2038,--disable-year2038"
