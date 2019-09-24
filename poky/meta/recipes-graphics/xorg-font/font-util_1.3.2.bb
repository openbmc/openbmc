SUMMARY = "X.Org font package creation/installation utilities"

require xorg-font-common.inc

#Unicode is MIT
LICENSE = "BSD & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5df208ec65eb84ce5bb8d82d8f3b9675 \
                    file://ucs2any.c;endline=28;md5=8357dc567fc628bd12696f15b2a33bcb \
                    file://bdftruncate.c;endline=26;md5=4f82ffc101a1b165eae9c6998abff937 \
                    file://map-ISO8859-1;beginline=9;endline=23;md5=1cecb984063248f29ffe5c46f5c04f34"

DEPENDS = "encodings util-macros"
DEPENDS_class-native = "util-macros-native"
RDEPENDS_${PN} = "mkfontdir mkfontscale encodings"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "3d6adb76fdd072db8c8fae41b40855e8"
SRC_URI[sha256sum] = "3ad880444123ac06a7238546fa38a2a6ad7f7e0cc3614de7e103863616522282"

SYSROOT_DIRS_BLACKLIST_remove = "${datadir}/fonts"
