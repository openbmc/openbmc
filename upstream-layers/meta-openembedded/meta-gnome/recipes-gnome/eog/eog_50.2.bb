SUMMARY = "This is the Eye of GNOME, an image viewer program."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/eog"

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

SRC_URI[archive.sha256sum] = "c6c2afdfe40a838de0c6b264884a80d388f1c2efa3f73a15ede08824c46be0b3"

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'x11','cms', '', d)}"

PACKAGECONFIG[cms] = "-Dcms=true,-Dcms=false,lcms"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dxmp=false"

FILES:${PN} += "${datadir}"
