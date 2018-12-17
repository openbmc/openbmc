# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=8b2933503c0443e041d3168dc0c65640"
require byacc.inc

SRC_URI[md5sum] = "97fdc0745e0ff0cef5c09d57d0c6752d"
SRC_URI[sha256sum] = "d0940dbffbc7e9c9dd4985c25349c390beede84ae1d9fe86b71c0aa659a6d693"

