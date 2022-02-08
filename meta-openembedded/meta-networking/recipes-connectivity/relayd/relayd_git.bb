DESCRIPTION = "Layer 3 relay daemon"
SECTION = "console/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://main.c;endline=17;md5=86aad799085683e0a2e1c2684a20bab2"

DEPENDS = "libubox"

SRC_URI = "git://git.openwrt.org/project/relayd.git;branch=master \
           file://0001-rtnl_flush-Error-on-failed-write.patch \
"

SRCREV = "ad0b25ad74345d367c62311e14b279f5ccb8ef13"
PV = "0.0.1+git${SRCPV}"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit cmake
