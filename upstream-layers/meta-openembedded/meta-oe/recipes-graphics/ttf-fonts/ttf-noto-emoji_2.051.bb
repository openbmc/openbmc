require ttf.inc

SUMMARY = "Google noto emoji font pack"
HOMEPAGE = "https://github.com/googlefonts/noto-emoji"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://fonts/LICENSE;md5=55719faa0112708e946b820b24b14097"

SRC_URI = "git://github.com/googlefonts/noto-emoji;branch=main;protocol=https"
SRCREV = "8998f5dd683424a73e2314a8c1f1e359c19e8742"
PE = "1"

PACKAGES =+ "${PN}-color ${PN}-color-noflags ${PN}-colrv1 ${PN}-colrv1-noflags"
FONT_PACKAGES = "${PN} ${PN}-color ${PN}-color-noflags ${PN}-colrv1 ${PN}-colrv1-noflags"

FILES:${PN} = "${datadir}/fonts/truetype/*.ttf"
FILES:${PN}-color = "${datadir}/fonts/truetype/NotoColorEmoji.ttf"
FILES:${PN}-color-noflags = "${datadir}/fonts/truetype/NotoColorEmoji-noflags.ttf"
# The COLRv1 format is a color scalable font compared with the previous color bitmap fonts.
# COLRv1 is significantly smaller than the color bitmap format (~5MB vs ~11MB).
FILES:${PN}-colrv1 = "${datadir}/fonts/truetype/Noto-COLRv1.ttf"
FILES:${PN}-colrv1-noflags = "${datadir}/fonts/truetype/Noto-COLRv1-noflags.ttf"

do_compile[noexec] = "1"
