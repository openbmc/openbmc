# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=6a728308869d7a7901618a5bcb970f7e"
require byacc.inc

SRC_URI[sha256sum] = "192c2fae048d4e7f514ba451627f9c4e612765099f819c19191f9fde3e609673"
