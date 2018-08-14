require xcb-util.inc

DEPENDS += "xcb-util"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://image/xcb_image.c;endline=24;md5=eafdf965cfb89955fdedf75054223fb4 \
                    file://image/xcb_image.h;beginline=4;endline=27;md5=48cd25ae55e7de525fe1e1a3a7672e1c"

SRC_URI += "file://clang.patch \
"

SRC_URI[md5sum] = "08fe8ffecc8d4e37c0ade7906b3f4c87"
SRC_URI[sha256sum] = "2db96a37d78831d643538dd1b595d7d712e04bdccf8896a5e18ce0f398ea2ffc"
