require xorg-app-common.inc

SUMMARY = "Runtime configuration and test of XInput devices"

DESCRIPTION = "Xinput is an utility for configuring and testing XInput devices"

LIC_FILES_CHKSUM = "file://COPYING;md5=881525f89f99cad39c9832bcb72e6fa5"

DEPENDS += " libxi libxrandr libxinerama"

SRC_URI_EXT = "xz"
SRC_URI[md5sum] = "8e4d14823b7cbefe1581c398c6ab0035"
SRC_URI[sha256sum] = "ad04d00d656884d133110eeddc34e9c69e626ebebbbab04dc95791c2907057c8"
