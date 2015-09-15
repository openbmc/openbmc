require xorg-app-common.inc

SUMMARY = "A program to create an index of X font files in a directory"

DESCRIPTION = "For each directory argument, mkfontdir reads all of the \
font files in the directory. The font names and related data are written \
out to the files \"fonts.dir\", \"fonts.scale\", and \"fonts.alias\".  \
The X server and font server use these files to find the available font \
files."

PE = "1"
PR = "${INC_PR}.0"

RDEPENDS_${PN} += "mkfontscale"
RDEPENDS_${PN}_class-native += "mkfontscale-native"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=b4fcf2b90cadbfc15009b9e124dc3a3f"

SRC_URI[md5sum] = "18c429148c96c2079edda922a2b67632"
SRC_URI[sha256sum] = "56d52a482df130484e51fd066d1b6eda7c2c02ddbc91fe6e2be1b9c4e7306530"
