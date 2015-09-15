SUMMARY = "Mobile Broadband Service Provider Database"
SECTION = "network"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=87964579b2a8ece4bc6744d2dc9a8b04"
SRCREV = "d06ebd314a7cdd087d38e4966c19de42c8c55246"
PV = "20140618+gitr${SRCPV}"
PE = "1"

SRC_URI = "git://git.gnome.org/mobile-broadband-provider-info"
S = "${WORKDIR}/git"

inherit autotools
