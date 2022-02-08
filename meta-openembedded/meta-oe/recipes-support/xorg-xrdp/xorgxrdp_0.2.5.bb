SUMMARY = "Xorg drivers for xrdp."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a2523660329fdca3d954c0a87390e007"

inherit autotools pkgconfig 

DEPENDS = "virtual/libx11 xserver-xorg xrdp nasm-native"

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11 pam"

SRC_URI = "git://github.com/neutrinolabs/xorgxrdp.git;branch=master;protocol=https"

SRCREV = "c122544f184d4031bbae1ad80fbab554c34a9427"

PV = "0.2.5"

S = "${WORKDIR}/git"

FILES_${PN} += "${libdir}/xorg/modules/*"

INSANE_SKIP_${PN} += "xorg-driver-abi"
