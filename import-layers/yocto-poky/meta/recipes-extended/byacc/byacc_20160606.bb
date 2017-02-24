# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=74533d32ffd38bca4cbf1f1305f8bc60"
require byacc.inc


SRC_URI[md5sum] = "d527c811b360f04a8c5f5a0a90625966"
SRC_URI[sha256sum] = "cc8fdced486cb70cec7a7c9358de836bfd267d19d6456760bb4721ccfea5ac91"
