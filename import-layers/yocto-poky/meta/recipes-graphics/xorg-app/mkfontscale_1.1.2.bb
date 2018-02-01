require xorg-app-common.inc

SUMMARY = "A program to create an index of scalable font files for X"

DESCRIPTION = "For each directory argument, mkfontscale reads all of the \
scalable font files in the directory. For every font file found, an X11 \
font name (XLFD) is generated, and is written together with the file \
name to a file fonts.scale in the directory.  The resulting fonts.scale \
is used by the mkfontdir program."

DEPENDS = "util-macros-native zlib libfontenc freetype xproto"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=2e0d129d05305176d1a790e0ac1acb7f"

SRC_URI[md5sum] = "9bdd6ebfa62b1bbd474906ac86a40fd8"
SRC_URI[sha256sum] = "8c6d5228af885477b9aec60ca6f172578e7d2de42234357af62fb00439453f20"
