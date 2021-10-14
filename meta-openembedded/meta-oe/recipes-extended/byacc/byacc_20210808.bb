# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=80ce98c6ab91cc4a93a9d2cfb7c14463"
require byacc.inc

SRC_URI[sha256sum] = "f158529be9d0594263c7f11a87616a49ea23e55ac63691252a2304fbbc7d3a83"
