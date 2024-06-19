SUMMARY = "Evince is a document viewer for document formats like pdf, ps, djvu"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=96f2f8d5ee576a2163977938ea36fa7b"
SECTION = "x11/office"
DEPENDS = " \
    adwaita-icon-theme \
    appstream-glib \
    cairo \
    desktop-file-utils-native \
    dbus \
    gdk-pixbuf \
    glib-2.0 \
    gnome-common-native \
    gnome-desktop \
    gsettings-desktop-schemas \
    gspell \
    gstreamer1.0-plugins-base \
    gtk+3 \
    libarchive \
    libhandy \
    libsecret \
    libxml2 \
    poppler \
    yelp-tools-native \
    zlib \
"


inherit gnomebase itstool gnome-help pkgconfig gsettings gobject-introspection gettext mime-xdg gi-docgen features_check gtk-icon-cache

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'opengl', '', d)}"

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI[archive.sha256sum] = "bc0d1d41b9d7ffc762e99d2abfafacbf745182f0b31d86db5eec8c67f5f3006b"

GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "-Dnautilus=true,-Dnautilus=false,nautilus"

RDEPENDS:${PN} += "glib-2.0-utils"
RRECOMMENDS:${PN} = "adwaita-icon-theme"

PACKAGES =+ "${PN}-nautilus-extension"

do_install:prepend() {
    sed -i -e 's|${B}/../${PN}-${PV}|${TARGET_DBGSRC_DIR}|g' ${B}/libview/ev-view-type-builtins.c
    sed -i -e 's|${B}/../${PN}-${PV}|${TARGET_DBGSRC_DIR}|g' ${B}/libdocument/ev-document-type-builtins.c
}

FILES:${PN} += "${datadir}/dbus-1 \
                ${datadir}/metainfo \
                ${datadir}/thumbnailers \
                ${systemd_user_unitdir} \
"
FILES:${PN}-nautilus-extension = "${libdir}/nautilus/*/*so"

CVE_PRODUCT = "evince"
CVE_STATUS[CVE-2011-0433] = "fixed-version: No action required. The current version (46.0) is not affected by the CVE which has been patched since version 3.1.2"
CVE_STATUS[CVE-2011-5244] = "fixed-version: No action required. The current version (46.0) is not affected by the CVE which has been patched since version 3.1.2"
