SUMMARY = "The Xorg font encoding files"

DESCRIPTION = "The encodings that map to specific characters for a \
number of Xorg and common fonts."

require xorg-font-common.inc
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=9da93f2daf2d5572faa2bfaf0dbd9e76"
PE = "1"
PR = "${INC_PR}.1"

DEPENDS = "mkfontscale-native font-util-native"
RDEPENDS_${PN} = ""

SRC_URI += "file://nocompiler.patch"

inherit allarch

EXTRA_OECONF += "--with-encodingsdir=${datadir}/fonts/X11/encodings"

SRC_URI[md5sum] = "0f2d6546d514c5cc4ecf78a60657a5c1"
SRC_URI[sha256sum] = "ced6312988a45d23812c2ac708b4595f63fd7a49c4dcd9f66bdcd50d1057d539"
