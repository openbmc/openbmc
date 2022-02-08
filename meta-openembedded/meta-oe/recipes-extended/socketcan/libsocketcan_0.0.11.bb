SUMMARY = "Control basic functions in socketcan from userspace"
HOMEPAGE = "http://www.pengutronix.de"
SECTION = "libs/network"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://src/libsocketcan.c;beginline=3;endline=17;md5=97e38adced4385d8fba1ae2437cedee1"

SRCREV = "0ff01ae7e4d271a7b81241e7a7026bfcea0add3f"

SRC_URI = "git://git.pengutronix.de/git/tools/libsocketcan.git;protocol=git;branch=master"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
