SUMMARY = "OpenGL Utility Toolkit"
LICENSE = "Kilgard"
LIC_FILES_CHKSUM = "file://include/GL/glut.h;md5=4aab626c830aed79d7b2e5a76dfa2dd6;startline=4;endline=8"

SRC_URI = "git://anongit.freedesktop.org/mesa/glut"

SRCREV = "ee89e9aeb49604b036e06f1df6478d32006b30cd"
PV = "8.0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "virtual/libx11 libxmu libxi virtual/libgl libglu"
