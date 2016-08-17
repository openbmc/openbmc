require recipes-graphics/xorg-driver/xorg-driver-video.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=e7f3e39474aeea5af381a8e103dafc36"

SUMMARY = "X.org server -- Geode GX2/LX display driver"
PR = "${INC_PR}.0"

SRC_URI += "file://0001-Add-config.h-include-to-src-lx_memory.c.patch"
SRC_URI[md5sum] = "35fa387f6a33f6b22a56ce4bda424392"
SRC_URI[sha256sum] = "38fc1f55e29fb7985b90c9021a4b7e5295d42888bd669174f42f3b7f681fc1a7"

COMPATIBLE_HOST = "i.86.*-linux"

RDEPENDS_${PN} += "xserver-xorg-module-exa"

# 2_2.11.16-r21.0/xf86-video-geode-2.11.16/src/gx_driver.c:376:20: error: implicit declaration of function 'xf86MapVidMem' [-Werror=implicit-function-declaration]
PNBLACKLIST[xf86-video-geode] ?= "BROKEN, fails to build"
