SUMMARY = "The Xorg font encoding files"

DESCRIPTION = "The encodings that map to specific characters for a \
number of Xorg and common fonts."

require xorg-font-common.inc
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=9da93f2daf2d5572faa2bfaf0dbd9e76"
PE = "1"

DEPENDS = "mkfontscale-native mkfontdir-native font-util-native"
RDEPENDS:${PN} = ""

SRC_URI += "file://nocompiler.patch"
SRC_URI[sha256sum] = "77e301de661f35a622b18f60b555a7e7d8c4d5f43ed41410e830d5ac9084fc26"

SRC_URI_EXT = "xz"

inherit allarch

EXTRA_OECONF += "--with-encodingsdir=${datadir}/fonts/X11/encodings"

# postinst from .inc doesn't apply to this recipe
pkg_postinst:${PN} () {
}
