SUMMARY = "The Xorg font encoding files"

DESCRIPTION = "The encodings that map to specific characters for a \
number of Xorg and common fonts."

require xorg-font-common.inc
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=9da93f2daf2d5572faa2bfaf0dbd9e76"
PE = "1"

DEPENDS = "mkfontscale-native mkfontdir-native font-util-native"
RDEPENDS:${PN} = ""

SRC_URI[sha256sum] = "3a39a9f43b16521cdbd9f810090952af4f109b44fa7a865cd555f8febcea70a4"

SRC_URI_EXT = "xz"

inherit allarch

EXTRA_OECONF += "--with-encodingsdir=${datadir}/fonts/X11/encodings"

# postinst from .inc doesn't apply to this recipe
pkg_postinst:${PN} () {
}
