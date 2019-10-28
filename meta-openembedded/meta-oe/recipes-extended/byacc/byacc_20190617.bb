# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=3eb7c635434fafe23ef30fc263e63b2f"
require byacc.inc

SRC_URI[md5sum] = "17b3f7e7ed570d785b145064d99df615"
SRC_URI[sha256sum] = "f87868167b920bf2cb30fc32b62f63ae15671181ef329226d1063100be02518d"

