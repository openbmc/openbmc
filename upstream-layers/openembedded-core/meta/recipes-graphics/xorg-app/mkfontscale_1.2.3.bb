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
SRC_URI[sha256sum] = "2921cdc344f1acee04bcd6ea1e29565c1308263006e134a9ee38cf9c9d6fe75e"
