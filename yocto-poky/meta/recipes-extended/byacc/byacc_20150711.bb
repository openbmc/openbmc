# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=c52fb2d1b3f75b3b7534927807a1b714"
require byacc.inc

SRC_URI[md5sum] = "2700401030583c4e9169ac7ea7d08de8"
SRC_URI[sha256sum] = "c354e4ee14c4a1bf11e55dde9275011d14887ef066406a088b6fa56caf039248"
