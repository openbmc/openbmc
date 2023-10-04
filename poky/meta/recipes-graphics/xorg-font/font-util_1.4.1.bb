SUMMARY = "X.Org font package creation/installation utilities"

require xorg-font-common.inc

LICENSE = "Unicode-TOU & BSD-4-Clause & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=2a9e705c00e463c8d294f90486852e06 \
                    file://ucs2any.c;endline=28;md5=8357dc567fc628bd12696f15b2a33bcb \
                    file://bdftruncate.c;endline=26;md5=4f82ffc101a1b165eae9c6998abff937 \
                    file://map-ISO8859-1;beginline=1;endline=4;md5=9c9c1d525d29c0e82b5c99edbb8e71c1 \
                    "

DEPENDS = "encodings util-macros"
DEPENDS:class-native = "util-macros-native"
RDEPENDS:${PN} = "mkfontdir mkfontscale encodings"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "5c9f64123c194b150fee89049991687386e6ff36ef2af7b80ba53efaf368cc95"

SYSROOT_DIRS_IGNORE:remove = "${datadir}/fonts"

SRC_URI_EXT = "xz"
