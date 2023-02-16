# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=b56b7454f5f865de2e6e35ee2185b461"
require byacc.inc

SRC_URI[sha256sum] = "576cc9d9ae5e22503ed5e3582498cf2cccacef401969106420547b4d05c87d76"
