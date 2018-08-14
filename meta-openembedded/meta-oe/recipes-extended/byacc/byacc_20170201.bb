# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=9176bfb16dab43ebcb8e50d9ee9550b6"
require byacc.inc

SRC_URI[md5sum] = "4bb274bbd7b648d4251c2b9ca36ed0c9"
SRC_URI[sha256sum] = "90b768d177f91204e6e7cef226ae1dc7cac831b625774cebd3e233a917754f91"

