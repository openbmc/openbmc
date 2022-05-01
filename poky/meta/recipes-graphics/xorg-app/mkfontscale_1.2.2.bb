require xorg-app-common.inc

SUMMARY = "A program to create an index of scalable font files for X"

DESCRIPTION = "For each directory argument, mkfontscale reads all of the \
scalable font files in the directory. For every font file found, an X11 \
font name (XLFD) is generated, and is written together with the file \
name to a file fonts.scale in the directory.  The resulting fonts.scale \
is used by the mkfontdir program."

DEPENDS = "util-macros-native zlib libfontenc freetype xorgproto"

PROVIDES += "mkfontdir"
RPROVIDES:${PN} += "mkfontdir"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=99b1e1269aba5179139b9e4380fc0934"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "8ae3fb5b1fe7436e1f565060acaa3e2918fe745b0e4979b5593968914fe2d5c4"
