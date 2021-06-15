require xorg-app-common.inc

SUMMARY = "Window information utility for X"

DESCRIPTION = "Xwininfo is a utility for displaying information about \
windows.  Information may include window position, size, color depth, \
and a number of other items."

LIC_FILES_CHKSUM = "file://COPYING;md5=78976cd3115f6faf615accc4e094d90e"
DEPENDS += "libxext libxmu gettext-native"

PE = "0"

SRC_URI[md5sum] = "26d46f7ef0588d3392da3ad5802be420"
SRC_URI[sha256sum] = "7a405441dfc476666c744f5fcd1bc8a75abf8b5b1d85db7b88b370982365080e"
