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

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase pkgconfig gsettings gobject-introspection gettext mime-xdg features_check gtk-icon-cache

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "9dcfdce5585a90e2bb1cf57e377cb1eb12d41bd9bcb9bbacdf506bc1b1354ef9"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dxmp=false"

FILES:${PN} += "${datadir}"
