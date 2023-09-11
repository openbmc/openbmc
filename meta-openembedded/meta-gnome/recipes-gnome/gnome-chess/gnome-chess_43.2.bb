SUMMARY = "GNOME Chess is a 2D chess game, where games can be played between a combination of human and computer players."
HOMEPAGE = "https://wiki.gnome.org/Apps/Chess"
LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data opengl"

GTKIC_VERSION = "4"

DEPENDS = " \
    appstream-glib-native \
    cairo \
    desktop-file-utils-native \
    glib-2.0 \
    glib-2.0 \
    gtk4 \
    libadwaita \
    librsvg \
    pango \
"

RRECOMMENDS:${PN}  = "gnuchess"

SRC_URI = "git://github.com/GNOME/gnome-chess.git;protocol=https;branch=master"

inherit meson pkgconfig gobject-introspection gtk-icon-cache vala features_check mime-xdg gsettings

GIR_MESON_OPTION = ""

S = "${WORKDIR}/git"
SRCREV = "de47d07ec6fc828b2668ca6ee59fe9bdfa9dc1d6"

FILES:${PN} += "${datadir}"
