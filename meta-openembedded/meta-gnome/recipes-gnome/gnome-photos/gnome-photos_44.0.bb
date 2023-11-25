SUMMARY = "Access, organize and share your photos on GNOME"
SECTION = "x11/gnome"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = " \
    cairo \
    glib-2.0-native \
    gdk-pixbuf-native \
    librsvg-native \
    gtk+3 \
    babl \
    dbus \
    gegl \
    geocode-glib \
    gexiv2 \
    gnome-online-accounts \
    gsettings-desktop-schemas \
    libdazzle \
    tracker \
    libhandy \
    libportal \
"

RDEPENDS:${PN} = "tracker-miners"


inherit gnomebase gettext gnome-help features_check

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

REQUIRED_DISTRO_FEATURES = "x11 opengl gobject-introspection-data"

PACKAGECONFIG ?= ""
PACKAGECONFIG[doc] = "-Dmanuals=true,-Dmanuals=false,libxslt-native docbook-xsl-stylesheets-native"

SRC_URI[archive.sha256sum] = "e78e210397d3c62809c6cd5521da6eccb4a11ddea5bf2af8632a47f4da5c829e"

do_install:append() {
    # make gnome-photos available on all desktops
    sed -i 's:OnlyShowIn=:#OnlyShowIn=:g' ${D}${datadir}/applications/org.gnome.Photos.desktop
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"

do_compile:append() {
    # glib-mkenums is embedding full paths into this file. There's no
    # option to it to use a sysroot style variable. So to avoid QA
    # errors, we sed WORKDIR out and make its includes relative
    sed -i "s|${B}||" src/photos-enums.h
    sed -i "s|${B}||" src/photos-enums.c
    sed -i "s|${B}||" src/photos-enums-gegl.c
    sed -i "s|${B}||" src/photos-enums-gegl.h
}
