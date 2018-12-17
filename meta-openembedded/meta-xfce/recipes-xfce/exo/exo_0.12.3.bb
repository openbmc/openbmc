DESCRIPTION="Application library for the Xfce desktop environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+ libxfce4util libxfce4ui virtual/libx11 liburi-perl-native cairo"
DEPENDS_class-native = "glib-2.0-native xfce4-dev-tools-native intltool-native"

inherit xfce pythonnative perlnative gtk-doc distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"
REQUIRED_DISTRO_FEATURES_class-native = ""

# SRC_URI must follow inherited one
SRC_URI += " \
    file://exo-no-tests-0.8.patch \
    file://configure.patch \
"

SRC_URI_append_class-native = " \
    file://reduce-build-to-exo-csource-only.patch \
"

SRC_URI[md5sum] = "da934c5939f2ee4540f5d48991053f01"
SRC_URI[sha256sum] = "b96fe86bb504aa9b3332b96105d755f5d9052c903fd6574b512a5ef76c0ad439"

PACKAGES =+ "exo-csource"

# Note: python bindings did not work in oe-dev and are about to be moved to
# pyxfce see http://comments.gmane.org/gmane.comp.desktop.xfce.devel.version4/19560
FILES_${PN} += "${datadir}/xfce4/ \
                ${libdir}/xfce4/exo-1 \
"

FILES_exo-csource += "${bindir}/exo-csource"

BBCLASSEXTEND = "native"
