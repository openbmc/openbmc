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

SRC_URI[md5sum] = "8809037bd48599af55dad81c508b6b39"
SRC_URI[sha256sum] = "30238ed915619e06ceb41721e5f747d67320555cc38d459e954839c189ccaf51"

EXTRA_OECONF = "--disable-xkb"
