# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=3eb7c635434fafe23ef30fc263e63b2f"
require byacc.inc

SRC_URI[md5sum] = "6745a4fbf0723c4c9280fc3e568b3d1b"
SRC_URI[sha256sum] = "071c2ebe36afaa8448b80e893473a681e63a3b8a4ed636c0d675780a02411cde"
