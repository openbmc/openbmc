# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=b56b7454f5f865de2e6e35ee2185b461"
require byacc.inc

SRC_URI[sha256sum] = "36b972a6d4ae97584dd186925fbbc397d26cb20632a76c2f52ac7653cd081b58"
