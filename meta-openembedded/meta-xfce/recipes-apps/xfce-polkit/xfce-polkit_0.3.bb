SUMMARY = "A simple PolicyKit authentication agent for XFCE"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=758b03f872a88c99fc36d50c5932091c"

DEPENDS = "libxfce4ui polkit"

inherit xfce-app features_check
REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = " \
    git://github.com/ncopa/${BPN}.git;branch=master;protocol=https \
"
SRCREV = "6d3282cc1734c305850d48f5bf4b4d94e88885e9"
S = "${WORKDIR}/git"
