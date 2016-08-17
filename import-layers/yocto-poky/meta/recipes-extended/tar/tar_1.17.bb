require tar.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PR = "r3"

SRC_URI += "file://m4extensions.patch \
    file://gcc43build.patch \
    file://avoid_heap_overflow.patch \
    "

SRC_URI[md5sum] = "c6c4f1c075dbf0f75c29737faa58f290"
SRC_URI[sha256sum] = "19f9021dda51a16295e4706e80870e71f87107675e51c176a491eba0fc4ca492"
