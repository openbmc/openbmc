SUMMARY = "Access, organize and share your photos on GNOME"
SECTION = "x11/gnome"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = " \
    glib-2.0-native \
    gdk-pixbuf-native \
    librsvg-native \
    gtk+3 \
    babl \
    gegl \
    geocode-glib \
    gnome-online-accounts \
    grilo \
    gsettings-desktop-schemas \
    libdazzle \
    libgdata \
    gfbgraph \
    tracker \
    libhandy \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext gnome-help features_check

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "25cb281425199dec7b045f13f32f8f96034cb0cb8b94d96f9dffaf4f5be68551"

do_install:append() {
    # make gnome-photos available on all desktops
    sed -i 's:OnlyShowIn=:#OnlyShowIn=:g' ${D}${datadir}/applications/org.gnome.Photos.desktop
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"

RRECOMMENDS:${PN} = "grilo-plugins"
