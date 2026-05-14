SUMMARY = "X.Org font package creation/installation utilities"

require xorg-font-common.inc

LICENSE = "Unicode-TOU & MIT & X11 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=756e7412ee04c80f5833cd2f35242f7a \
                    file://ucs2any.c;endline=28;md5=8357dc567fc628bd12696f15b2a33bcb \
                    file://bdftruncate.c;endline=26;md5=4f82ffc101a1b165eae9c6998abff937 \
                    file://map-ISO8859-1;beginline=1;endline=4;md5=9c9c1d525d29c0e82b5c99edbb8e71c1 \
                    "

DEPENDS = "encodings util-macros"
DEPENDS:class-native = "util-macros-native"
RDEPENDS:${PN} = "mkfontdir mkfontscale encodings"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "02e4f8afdcf03cc8372ca9c37aa104b1e36b47722dbc79531be08f0a4c622999"

SYSROOT_DIRS_IGNORE:remove = "${datadir}/fonts"

SRC_URI_EXT = "xz"
