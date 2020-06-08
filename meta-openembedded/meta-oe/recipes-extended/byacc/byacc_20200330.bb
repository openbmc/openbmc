# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=262857ec5923f073fa06a1f1812225ea"
require byacc.inc

SRC_URI[md5sum] = "decf6e6b82ea2efae6bbad32d7777da8"
SRC_URI[sha256sum] = "e099e2dd8a684d739ac6b9a0e43d468314a5bc34fd21466502d120b18df51fb0"
