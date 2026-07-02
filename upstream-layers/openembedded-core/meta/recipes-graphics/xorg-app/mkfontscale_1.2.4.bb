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

LIC_FILES_CHKSUM = "file://COPYING;md5=5a60c596d1b5f3dee9f005b703b3180d"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "a01492a17a9b6c0ee3f92ee578850e305315b9f298da5f006a1cd4b51db01a5e"
