SUMMARY = "Mobile Broadband Service Provider Database"
HOMEPAGE = "http://live.gnome.org/NetworkManager/MobileBroadband/ServiceProviders"
SECTION = "network"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=87964579b2a8ece4bc6744d2dc9a8b04"
SRCREV = "befcbbc9867e742ac16415660b0b7521218a530c"
PV = "20170310"
PE = "1"

SRC_URI = "git://git.gnome.org/mobile-broadband-provider-info"
S = "${WORKDIR}/git"

inherit autotools
