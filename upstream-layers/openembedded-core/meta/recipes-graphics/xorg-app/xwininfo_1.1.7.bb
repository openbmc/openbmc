require xorg-app-common.inc

SUMMARY = "Window information utility for X"

DESCRIPTION = "Xwininfo is a utility for displaying information about \
windows.  Information may include window position, size, color depth, \
and a number of other items."

LIC_FILES_CHKSUM = "file://COPYING;md5=a1b9559d7b7997a6e9588012ebf8769a"
DEPENDS += "libxext libxmu virtual/libiconv"

PE = "0"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "bee14d594cc86cc59aae1015c1b452a71bf60c304131e2716ca1cf0df733b4ac"

XORGBUILDCLASS = "meson"
