require xorg-app-common.inc

SUMMARY = "Runtime configuration and test of XInput devices"

DESCRIPTION = "Xinput is an utility for configuring and testing XInput devices"

LIC_FILES_CHKSUM = "file://COPYING;md5=881525f89f99cad39c9832bcb72e6fa5"

DEPENDS += " libxi libxrandr libxinerama"


SRC_URI[md5sum] = "305980ac78a6954e306a14d80a54c441"
SRC_URI[sha256sum] = "b7632d0f228a8a6be93b09857ea413940fcf44091e60f4a0fe9f5fd82efd871f"

