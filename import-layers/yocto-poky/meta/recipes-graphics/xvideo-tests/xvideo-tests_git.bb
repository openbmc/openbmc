SUMMARY = "Simple XVideo test application"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://src/test-xvideo.c;beginline=1;endline=20;md5=6ae3b4c3c2ff9e51dbbc35bb237afa00"
DEPENDS = "libxv"

SRCREV = "7d38b881e99eb74169d292b40f7164e461a65092"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/test-xvideo"

S = "${WORKDIR}/git"

inherit autotools distro_features_check

# The libxv requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
