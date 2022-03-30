DESCRIPTION = "Application library for the Xfce desktop environment"
SECTION = "x11"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+3 libxfce4ui virtual/libx11 liburi-perl-native cairo"

inherit xfce perlnative gtk-doc features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

# SRC_URI must follow inherited one
SRC_URI += " \
    file://exo-no-tests-0.8.patch \
    file://configure.patch \
"

SRC_URI[sha256sum] = "722dff3c3fe23f0a65405e63889cf247c99d092d3f9fb16dec78d062cfb8fae6"

# Note: python bindings did not work in oe-dev and are about to be moved to
# pyxfce see http://comments.gmane.org/gmane.comp.desktop.xfce.devel.version4/19560
FILES:${PN} += " \
    ${datadir}/xfce4/ \
    ${libdir}/xfce4/exo* \
"
