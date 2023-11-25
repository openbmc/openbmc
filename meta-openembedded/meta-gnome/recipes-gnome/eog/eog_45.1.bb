SUMMARY = "This is the Eye of GNOME, an image viewer program."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    librsvg \
    gnome-desktop \
    gsettings-desktop-schemas \
    gdk-pixbuf \
    gtk+3 \
    libhandy \
    libpeas \
    libportal \
    libexif \
    lcms \
"


inherit gnomebase pkgconfig gsettings gobject-introspection gettext mime-xdg features_check gtk-icon-cache

# FIXME: whilst eog uses libpeas <2, g-i is needed. This can be removed when libpeas2 is used.
REQUIRED_DISTRO_FEATURES = "opengl gobject-introspection-data"

SRC_URI[archive.sha256sum] = "c17f86704c722b36c01a168f1e51439be0bb279f2c6646f48b66e9d0f3a24cd2"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dxmp=false"

FILES:${PN} += "${datadir}"
