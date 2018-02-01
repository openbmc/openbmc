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

SRC_URI[md5sum] = "331b3a2a3a1a78b5b44cfbd43f86fcfe"
SRC_URI[sha256sum] = "f5a3c824761df351ca91827ac221090943ef28b248573486050de89f4bfcdc4c"

XORG_PN = "libXft"

BBCLASSEXTEND = "native"

python () {
        if d.getVar('DEBIAN_NAMES'):
            d.setVar('PKG_${PN}', '${MLPREFIX}libxft2')
}
