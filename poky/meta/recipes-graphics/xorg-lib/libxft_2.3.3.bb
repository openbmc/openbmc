SUMMARY = "XFt: X FreeType libary"

DESCRIPTION = "Xft was designed to provide good support for scalable \
fonts, and to do so efficiently.  Unlike the core fonts system, it \
supports features such as anti-aliasing and sub-pixel rasterisation. \
Perhaps more importantly, it gives applications full control over the \
way glyphs are rendered, making fine typesetting and WYSIWIG display \
possible. Finally, it allows applications to use fonts that are not \
installed system-wide for displaying documents with embedded fonts.  Xft \
is not compatible with the core fonts system: usage of Xft requires \
fairly extensive changes to toolkits (user-interface libraries)."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=90b90b60eb30f65fc9c2673d7cf59e24"

DEPENDS += "virtual/libx11 libxrender freetype fontconfig"
PROVIDES = "xft"

PE = "1"

SRC_URI[md5sum] = "4a433c24627b4ff60a4dd403a0990796"
SRC_URI[sha256sum] = "225c68e616dd29dbb27809e45e9eadf18e4d74c50be43020ef20015274529216"

XORG_PN = "libXft"

BBCLASSEXTEND = "native nativesdk"

python () {
        if d.getVar('DEBIAN_NAMES'):
            d.setVar('PKG_${PN}', '${MLPREFIX}libxft2')
}
