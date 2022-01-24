SUMMARY = "Xorg drivers for xrdp."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a2523660329fdca3d954c0a87390e007"

inherit autotools pkgconfig 

DEPENDS = "virtual/libx11 xserver-xorg xrdp nasm-native"

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11 pam"

SRC_URI = "git://github.com/neutrinolabs/xorgxrdp.git;branch=devel;protocol=https"

SRCREV = "752b7659773a11ca6d4600645547c06fdb8a23d9"

PV = "0.2.18"

S = "${WORKDIR}/git"

FILES:${PN} += "${libdir}/xorg/modules/*"

INSANE_SKIP:${PN} += "xorg-driver-abi"
