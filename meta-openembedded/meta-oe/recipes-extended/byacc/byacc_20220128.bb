# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=62c37a10a2faf90235ee64280ad72737"
require byacc.inc

SRC_URI[sha256sum] = "42c1805cc529314e6a76326fe1b33e80c70862a44b01474da362e2f7db2d749c"
