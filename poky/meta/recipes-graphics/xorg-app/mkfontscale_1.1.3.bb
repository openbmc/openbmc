require xorg-app-common.inc

SUMMARY = "A program to create an index of scalable font files for X"

DESCRIPTION = "For each directory argument, mkfontscale reads all of the \
scalable font files in the directory. For every font file found, an X11 \
font name (XLFD) is generated, and is written together with the file \
name to a file fonts.scale in the directory.  The resulting fonts.scale \
is used by the mkfontdir program."

DEPENDS = "util-macros-native zlib libfontenc freetype xorgproto"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=2e0d129d05305176d1a790e0ac1acb7f"

SRC_URI[md5sum] = "987c438e79f5ddb84a9c5726a1610819"
SRC_URI[sha256sum] = "1e98df69ee5f4542d711e140e1d93e2c3f2775407ccbb7849110d52b91782a6a"
