require recipes-graphics/xorg-app/xorg-app-common.inc
SUMMARY = "A program to compile XKB keyboard description"
DESCRIPTION = "The  xkbevd event daemon listens for specified XKB \
events and executes requested commands if they occur. "

LIC_FILES_CHKSUM = "file://COPYING;md5=208668fa9004709ba22c2b748140956c"

DEPENDS += "libxkbfile bison-native"

BBCLASSEXTEND = "native"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "38357b702de9d3457c4ff75053390f457b84c4accc7f088101255c37c684926b"
