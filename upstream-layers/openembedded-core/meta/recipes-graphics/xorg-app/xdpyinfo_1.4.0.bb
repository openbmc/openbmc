require xorg-app-common.inc

SUMMARY = "Display information utility for X"

DESCRIPTION = "Xdpyinfo is a utility for displaying information about an \
X server. It is used to examine the capabilities of a server, the \
predefined values for various parameters used in communicating between \
clients and the server, and the different types of screens and visuals \
that are available."

LIC_FILES_CHKSUM = "file://COPYING;md5=f3d09e6b9e203a1af489e16c708f4fb3"
DEPENDS += "libxtst libxext libxxf86vm libxi libxrender libxinerama libxau libxcomposite"
PE = "1"

SRC_URI += "file://disable-xkb.patch"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "dc1de6e6e091ed46c4837b0ae9811e8182f7be0d2af638cab3e530ff081a48b6"

EXTRA_OECONF = "--disable-xkb"
