DESCRIPTION = "Layer 3 relay daemon"
SECTION = "console/network"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://main.c;endline=17;md5=86aad799085683e0a2e1c2684a20bab2"

DEPENDS = "libubox"

SRC_URI = "git://git.openwrt.org/project/relayd.git;branch=master"

SRCREV = "f646ba40489371e69f624f2dee2fc4e19ceec00e"
PV = "0.0.1+git"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit cmake

CFLAGS:append:toolchain-clang = " -Wno-error=gnu-variable-sized-type-not-at-end"
