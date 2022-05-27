SUMMARY = "Mobile Broadband Service Provider Database"
HOMEPAGE = "http://live.gnome.org/NetworkManager/MobileBroadband/ServiceProviders"
DESCRIPTION = "Mobile Broadband Service Provider Database stores service provider specific information. When this Database is available the information can be fetched there"
SECTION = "network"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=87964579b2a8ece4bc6744d2dc9a8b04"

SRCREV = "3d5c8d0f7e0264768a2c000d0fd4b4d4a991e041"
PV = "20220511"
PE = "1"

SRC_URI = "git://gitlab.gnome.org/GNOME/mobile-broadband-provider-info.git;protocol=https;branch=main"
S = "${WORKDIR}/git"

inherit autotools

DEPENDS += "libxslt-native"
