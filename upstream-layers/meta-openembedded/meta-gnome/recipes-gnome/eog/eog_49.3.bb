SUMMARY = "This is the Eye of GNOME, an image viewer program."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    itstool-native \
    librsvg \
    gnome-desktop \
    gsettings-desktop-schemas \
    gdk-pixbuf \
    gtk+3 \
    libhandy \
    libpeas-1 \
    libportal \
    libexif \
"


inherit gnomebase pkgconfig gsettings gobject-introspection gettext mime-xdg features_check gtk-icon-cache

# FIXME: whilst eog uses libpeas <2, g-i is needed. This can be removed when libpeas2 is used.
REQUIRED_DISTRO_FEATURES = "opengl gobject-introspection-data"

SRC_URI[archive.sha256sum] = "6b4e69c7a8086ae85d556ca4a24daa665b5622c4097e1ee59564bff4cc963124"

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'x11','cms', '', d)}"

PACKAGECONFIG[cms] = "-Dcms=true,-Dcms=false,lcms"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dxmp=false"

FILES:${PN} += "${datadir}"
