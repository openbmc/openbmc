DESCRIPTION = "Layer 3 relay daemon"
SECTION = "console/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://main.c;endline=17;md5=86aad799085683e0a2e1c2684a20bab2"

DEPENDS = "libubox"

SRC_URI = "git://nbd.name/relayd.git"

SRCREV = "2970ff60bac6b70ecb682779d5c776dc559dc0b9"

S = "${WORKDIR}/git"

inherit cmake
