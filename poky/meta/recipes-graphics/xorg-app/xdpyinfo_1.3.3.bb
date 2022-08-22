require xorg-app-common.inc

SUMMARY = "Display information utility for X"

DESCRIPTION = "Xdpyinfo is a utility for displaying information about an \
X server. It is used to examine the capabilities of a server, the \
predefined values for various parameters used in communicating between \
clients and the server, and the different types of screens and visuals \
that are available."

LIC_FILES_CHKSUM = "file://COPYING;md5=f3d09e6b9e203a1af489e16c708f4fb3"
DEPENDS += "libxtst libxext libxxf86vm libxi libxrender libxinerama libdmx libxau libxcomposite"
PE = "1"

SRC_URI += "file://disable-xkb.patch"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "356d5fd62f3e98ee36d6becf1b32d4ab6112d618339fb4b592ccffbd9e0fc206"

EXTRA_OECONF = "--disable-xkb"
