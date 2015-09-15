PR = "r1"

LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f \
                    file://tests/COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
                    file://glob/COPYING.LIB;md5=4a770b67e6be0f60da244beb2de0fce4"

require make.inc

SRC_URI += "file://make_fix_for_automake-1.12.patch"
SRC_URI += "file://makeinfo.patch"

SRC_URI[md5sum] = "354853e0b2da90c527e35aabb8d6f1e6"
SRC_URI[sha256sum] = "f3e69023771e23908f5d5592954d8271d3d6af09693cecfd29cee6fde8550dc8"

