# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=3eb7c635434fafe23ef30fc263e63b2f"
require byacc.inc

SRC_URI[md5sum] = "84ffe59166f67bbe147a6f502c7af309"
SRC_URI[sha256sum] = "d291fb34816f45079067366b7f7300ffbf9f7e3f1aaf6d509b84442d065d11b9"

