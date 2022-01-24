SUMMARY = "Access, organize and share your photos on GNOME"
SECTION = "x11/gnome"
LICENSE = "GPLv3"
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

SRC_URI += " file://0001-meson-remove-incorrect-args-for-i18n.merge_file.patch"
SRC_URI[archive.sha256sum] = "e02d73e138af8b2868b5cad7faa1bdd278aeade3b6c3c92836511a4e6f3af1af"

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
