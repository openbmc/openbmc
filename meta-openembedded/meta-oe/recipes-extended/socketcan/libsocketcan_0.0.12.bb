SUMMARY = "Control basic functions in socketcan from userspace"
HOMEPAGE = "http://www.pengutronix.de"
SECTION = "libs/network"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://src/libsocketcan.c;beginline=3;endline=17;md5=97e38adced4385d8fba1ae2437cedee1"

SRCREV = "077def398ad303043d73339112968e5112d8d7c8"

SRC_URI = "git://git.pengutronix.de/git/tools/libsocketcan.git;protocol=git;branch=master"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[no-error-log] = "--disable-error-log,--enable-error-log"
