# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=74533d32ffd38bca4cbf1f1305f8bc60"
require byacc.inc


SRC_URI[md5sum] = "48ef38447f2cc864c70ef864b26cf817"
SRC_URI[sha256sum] = "30dc58cfcdb708eea7ba022db29b41d2d392f20727491b956954366f2f2117f0"
