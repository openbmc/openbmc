require xorg-app-common.inc

SUMMARY = "Window information utility for X"

DESCRIPTION = "Xwininfo is a utility for displaying information about \
windows.  Information may include window position, size, color depth, \
and a number of other items."

LIC_FILES_CHKSUM = "file://COPYING;md5=a1b9559d7b7997a6e9588012ebf8769a"
DEPENDS += "libxext libxmu gettext-native"

PE = "0"

SRC_URI_EXT = "xz"
SRC_URI[md5sum] = "c91201bc1eb5e7b38933be8d0f7f16a8"
SRC_URI[sha256sum] = "3518897c17448df9ba99ad6d9bb1ca0f17bc0ed7c0fd61281b34ceed29a9253f"
