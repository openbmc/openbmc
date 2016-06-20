SUMMARY = "Mobile Broadband Service Provider Database"
SECTION = "network"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=87964579b2a8ece4bc6744d2dc9a8b04"
SRCREV = "519465766fabc85b9fdea5f2b5ee3d08c2b1f70d"
PV = "20151214"
PE = "1"

SRC_URI = "git://git.gnome.org/mobile-broadband-provider-info"
S = "${WORKDIR}/git"

inherit autotools
