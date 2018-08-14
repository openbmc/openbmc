SUMMARY = "Control basic functions in socketcan from userspace"
HOMEPAGE = "http://www.pengutronix.de"
SECTION = "libs/network"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://src/libsocketcan.c;beginline=3;endline=17;md5=97e38adced4385d8fba1ae2437cedee1"

SRCREV = "51f1610160a1707f026f8c2d714a6f7aa3ca232b"

SRC_URI = "git://git.pengutronix.de/git/tools/libsocketcan.git;protocol=git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
