SUMMARY = "Gigolo is a frontend to easily manage connections to remote filesystems using GIO/GVfs"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "gtk+3 intltool-native xfce4-dev-tools-native"

inherit xfce-app

SRC_URI[md5sum] = "273111f5d7c26898e5ddea7b97d5dfe1"
SRC_URI[sha256sum] = "97a301aff012a143d0b99e7ecbb27084d3872aa203a74745e8357aab3a1880dc"
