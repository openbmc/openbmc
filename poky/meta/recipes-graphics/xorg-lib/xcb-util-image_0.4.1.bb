require xcb-util.inc

DEPENDS += "xcb-util"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://image/xcb_image.c;endline=24;md5=eafdf965cfb89955fdedf75054223fb4 \
                    file://image/xcb_image.h;beginline=4;endline=27;md5=48cd25ae55e7de525fe1e1a3a7672e1c"

SRC_URI += "file://clang.patch \
"

SRC_URI[sha256sum] = "ccad8ee5dadb1271fd4727ad14d9bd77a64e505608766c4e98267d9aede40d3d"
