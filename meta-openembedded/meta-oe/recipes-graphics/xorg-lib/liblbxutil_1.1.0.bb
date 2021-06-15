require recipes-graphics/xorg-lib/xorg-lib-common.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=b0d5bdc98f7ebab3b6c3791d9bf40907"

SUMMARY = "XFIXES Extension"
DEPENDS += " xorgproto zlib"
PE = "1"
PR = "r11"

SRC_URI += "file://mkg3states-1.1.patch \
            file://0001-lbx_zlib-Mark-declration-with-extern.patch \
           "
SRC_URI[md5sum] = "273329a78c2e9ea189ac416c7fde94a1"
SRC_URI[sha256sum] = "c6b6ff7858ec619cafa8205debca6bf78c5610a2844a782ed643c7fd017cf8ae"

export CC_FOR_BUILD = "gcc"
