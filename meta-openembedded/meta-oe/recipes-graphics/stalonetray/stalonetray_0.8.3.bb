SUMMARY = "Stand-alone system tray"
DESCRIPTION = "Stalonetray is a stand-alone freedesktop.org and KDE system tray"
SECTION = "x11/graphics"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "virtual/libx11"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "ae95dbbacc2620e032acea8abab8c9ef"
SRC_URI[sha256sum] = "36548a588b2d466913423245dda6ffb6313132cd0cec635a117d37b3dab5fd4c"

inherit autotools features_check

REQUIRED_DISTRO_FEATURES = "x11"
