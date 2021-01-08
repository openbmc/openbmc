DESCRIPTION = "Application library for the Xfce desktop environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+3 libxfce4ui virtual/libx11 liburi-perl-native cairo"

inherit xfce perlnative gtk-doc features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

# SRC_URI must follow inherited one
SRC_URI += " \
    file://exo-no-tests-0.8.patch \
    file://configure.patch \
"

SRC_URI[sha256sum] = "1975b00eed9a8aa1f899eab2efaea593731c19138b83fdff2f13bdca5275bacc"

# Note: python bindings did not work in oe-dev and are about to be moved to
# pyxfce see http://comments.gmane.org/gmane.comp.desktop.xfce.devel.version4/19560
FILES_${PN} += " \
    ${datadir}/xfce4/ \
    ${libdir}/xfce4/exo* \
"
