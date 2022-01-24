# Sigh. This is one of those places where everyone licenses it differently. Someone
# even apply UCB to it (Free/Net/OpenBSD). The maintainer states that:
# "I've found no reliable source which states that byacc must bear a UCB copyright."
# Setting to PD as this is what the upstream has it as.

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://package/debian/copyright;md5=e13dd32663e3159031608d536530a080"
require byacc.inc

SRC_URI[sha256sum] = "7bc42867a095df2189618b64497016298818e88e513fca792cb5adc9a68ebfb8"
