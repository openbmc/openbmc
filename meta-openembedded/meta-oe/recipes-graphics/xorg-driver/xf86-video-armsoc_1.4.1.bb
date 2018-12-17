require recipes-graphics/xorg-driver/xorg-driver-video.inc

SUMMARY = "X.Org X server -- ARM SOC display driver"
LICENSE = "MIT-X & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=10ce5de3b111315ea652a5f74ec0c602"

DEPENDS += "virtual/libx11 libdrm xorgproto"

SRCREV = "8bbdb2ae3bb8ef649999a8da33ddbe11a04763b8"
SRC_URI = "git://anongit.freedesktop.org/xorg/driver/xf86-video-armsoc"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "xserver-xorg-module-exa"
