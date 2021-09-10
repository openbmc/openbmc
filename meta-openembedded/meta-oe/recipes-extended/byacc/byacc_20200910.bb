# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=262857ec5923f073fa06a1f1812225ea"
require byacc.inc

SRC_URI[md5sum] = "5726a5a655c6fd4e8f950552cceeae29"
SRC_URI[sha256sum] = "0a5906073aeaf23ddc20aaac0ea61cb5ccc18572870b113375dec4ffe85ecf30"
