require ttf.inc

SUMMARY = "Google noto emoji font pack"
HOMEPAGE = "https://github.com/googlefonts/noto-emoji"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://fonts/LICENSE;md5=55719faa0112708e946b820b24b14097"

SRC_URI = "git://github.com/googlefonts/noto-emoji;protocol=https;branch=master"
SRCREV = "833a43d03246a9325e748a2d783006454d76ff66"

PACKAGES = "${PN}-color ${PN}-regular"
FONT_PACKAGES = "${PN}-color ${PN}-regular"

S = "${WORKDIR}/git"

FILES_${PN}-color = "${datadir}/fonts/truetype/NotoColorEmoji.ttf"
FILES_${PN}-regular = "${datadir}/fonts/truetype/NotoEmoji-Regular.ttf"

do_compile[noexec] = "1"
