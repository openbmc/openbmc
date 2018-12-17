require xorg-app-common.inc

SUMMARY = "Window information utility for X"

DESCRIPTION = "Xwininfo is a utility for displaying information about \
windows.  Information may include window position, size, color depth, \
and a number of other items."

LIC_FILES_CHKSUM = "file://COPYING;md5=78976cd3115f6faf615accc4e094d90e"
DEPENDS += "libxext libxmu gettext-native"

PE = "0"

SRC_URI[md5sum] = "9a505b91ae7160bbdec360968d060c83"
SRC_URI[sha256sum] = "839498aa46b496492a5c65cd42cd2e86e0da88149b0672e90cb91648f8cd5b01"
