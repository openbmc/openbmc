SUMMARY = "X.Org font package creation/installation utilities"

require xorg-font-common.inc

#Unicode is MIT
LICENSE = "MIT & MIT & BSD-4-Clause & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5df208ec65eb84ce5bb8d82d8f3b9675 \
                    file://ucs2any.c;endline=28;md5=8357dc567fc628bd12696f15b2a33bcb \
                    file://bdftruncate.c;endline=26;md5=4f82ffc101a1b165eae9c6998abff937 \
                    file://map-ISO8859-1;beginline=9;endline=23;md5=1cecb984063248f29ffe5c46f5c04f34"

DEPENDS = "encodings util-macros"
DEPENDS:class-native = "util-macros-native"
RDEPENDS:${PN} = "mkfontdir mkfontscale encodings"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "9f724bf940128c7e39f7252bd961cd38cfac2359de2100a8bed696bf40d40f7d"

SYSROOT_DIRS_IGNORE:remove = "${datadir}/fonts"

SRC_URI_EXT = "xz"
