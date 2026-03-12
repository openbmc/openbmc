# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=0eedc51db8ab59ff33e74b4e8138035c"
require byacc.inc

SRC_URI[sha256sum] = "b618c5fb44c2f5f048843db90f7d1b24f78f47b07913c8c7ba8c942d3eb24b00"
