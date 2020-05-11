DESCRIPTION = "Application library for the Xfce desktop environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+3 libxfce4ui virtual/libx11 liburi-perl-native cairo"
DEPENDS_class-native = "glib-2.0-native xfce4-dev-tools-native intltool-native"

inherit xfce perlnative gtk-doc features_check mime-xdg

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

SRC_URI[md5sum] = "efeb039d64b3257e39a1a38e75eb19b1"
SRC_URI[sha256sum] = "ec892519c08a67f3e0a1f0f8d43446e26871183e5aa6be7f82e214f388d1e5b6"

PACKAGECONFIG ??= ""
PACKAGECONFIG_class-target ??= "gtk"
PACKAGECONFIG[gtk] = "--enable-gtk2,--disable-gtk2,gtk+"

PACKAGES =+ "exo-csource"

# Note: python bindings did not work in oe-dev and are about to be moved to
# pyxfce see http://comments.gmane.org/gmane.comp.desktop.xfce.devel.version4/19560
FILES_${PN} += " \
    ${datadir}/xfce4/ \
    ${libdir}/xfce4/exo* \
"

FILES_exo-csource += "${bindir}/exo-csource"

BBCLASSEXTEND = "native"
