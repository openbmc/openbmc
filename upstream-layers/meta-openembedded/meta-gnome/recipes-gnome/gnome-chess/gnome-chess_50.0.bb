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
    gtk4 \
    itstool-native \
    libadwaita \
    librsvg \
    pango \
"

RRECOMMENDS:${PN}  = "gnuchess"

SRC_URI = "git://github.com/GNOME/gnome-chess.git;protocol=https;branch=main;tag=${PV}"

inherit meson pkgconfig gobject-introspection gtk-icon-cache vala features_check mime-xdg gsettings

GIR_MESON_OPTION = ""

SRCREV = "079ef83fe6c538be9178b03999724d5f4649bc69"

FILES:${PN} += "${datadir}"
