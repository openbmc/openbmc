SUMMARY = "The Xorg font encoding files"

DESCRIPTION = "The encodings that map to specific characters for a \
number of Xorg and common fonts."

require xorg-font-common.inc
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=9da93f2daf2d5572faa2bfaf0dbd9e76"
PE = "1"
PR = "${INC_PR}.1"

DEPENDS = "mkfontscale-native mkfontdir-native font-util-native"
RDEPENDS_${PN} = ""

SRC_URI += "file://nocompiler.patch"
SRC_URI[md5sum] = "bbae4f247b88ccde0e85ed6a403da22a"
SRC_URI[sha256sum] = "bd96e16143a044b19e87f217cf6a3763a70c561d1076aad6f6d862ec41774a31"

inherit allarch

EXTRA_OECONF += "--with-encodingsdir=${datadir}/fonts/X11/encodings"

# postinst from .inc doesn't apply to this recipe
pkg_postinst_${PN} () {
}
