require xorg-app-common.inc

SUMMARY = "Runtime configuration and test of XInput devices"

DESCRIPTION = "Xinput is an utility for configuring and testing XInput devices"

LIC_FILES_CHKSUM = "file://COPYING;md5=881525f89f99cad39c9832bcb72e6fa5"

DEPENDS += " libxi libxrandr libxinerama"

SRC_URI[md5sum] = "6a889412eff2e3c1c6bb19146f6fe84c"
SRC_URI[sha256sum] = "3694d29b4180952fbf13c6d4e59541310cbb11eef5bf888ff3d8b7f4e3aee5c4"
