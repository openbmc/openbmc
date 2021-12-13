SUMMARY = "Mobile Broadband Service Provider Database"
HOMEPAGE = "http://live.gnome.org/NetworkManager/MobileBroadband/ServiceProviders"
DESCRIPTION = "Mobile Broadband Service Provider Database stores service provider specific information. When this Database is available the information can be fetched there"
SECTION = "network"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=87964579b2a8ece4bc6744d2dc9a8b04"
SRCREV = "11f2247eccd3c161b8fd9b41143862e9fb81193c"
PV = "20210805"
PE = "1"

SRC_URI = "git://gitlab.gnome.org/GNOME/mobile-broadband-provider-info.git;protocol=https;branch=master"
S = "${WORKDIR}/git"

inherit autotools

DEPENDS += "libxslt-native"
