SUMMARY = "tail for multiple files in parallel"
DESCRIPTION = "\
    MultiTail allows you to monitor logfiles and command output in multiple \
    windows in a terminal, colorize, filter and merge \
"
HOMEPAGE = "https://vanheusden.com/multitail/screenshots.html"
BUGTRACKER = "https://github.com/folkertvanheusden/multitail/issues"
SECTION = "console/utils"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4d13123d158ecd203b5b9ffe50a0c76"

SRC_URI = "git://github.com/folkertvanheusden/multitail.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "1bf273ec425a047d278023d6c197a939243a9eb3"

DEPENDS = "ncurses"

inherit cmake

# Work around the implicit declaration of function 'waddnwstr' in mt.c:712
CFLAGS += "-Wno-error=implicit-function-declaration"

EXTRA_OECMAKE += "\
    -DCOMPILER_WARNINGS_ARE_ERRORS=OFF \
    -DUSE_CPPCHECK=OFF \
"

PACKAGECONFIG ?= "utf8"

PACKAGECONFIG[utf8] = "-DUTF8_SUPPORT=ON,-DUTF8_SUPPORT=OFF"

do_install:append() {
    mv -v ${D}${prefix}/etc ${D}${sysconfdir}
    mv -v ${D}${sysconfdir}/multitail.conf.new ${D}${sysconfdir}/multitail.conf
}
