SUMMARY = "Evince is a document viewer for document formats like pdf, ps, djvu"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=96f2f8d5ee576a2163977938ea36fa7b"
SECTION = "x11/office"
DEPENDS = " \
    gnome-common-native \
    yelp-tools-native \
    appstream-glib \
    gtk+3 \
    gspell \
    libsecret \
    poppler \
    gstreamer1.0-plugins-base \
    orc \
    adwaita-icon-theme \
    libhandy \
    ${@bb.utils.contains('DISTRO_FEATURES','x11','gnome-desktop3','',d)} \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase itstool gnome-help pkgconfig gsettings gobject-introspection gettext mime-xdg gtk-doc

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI[archive.sha256sum] = "33420500e0e060f178a435063197d42dae7b67e39cc437a96510a33ddf7e95fb"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = " \
    -Dsystemduserunitdir=no \
"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/libdocument/.libs"
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "-Dnautilus=true,-Dnautilus=false,nautilus"
PACKAGECONFIG[browser-plugin] = "-Dbrowser_plugin=true,-Dbrowser_plugin=false"

RDEPENDS:${PN} += "glib-2.0-utils"
RRECOMMMENDS_${PN} = "adwaita-icon-theme"

PACKAGES =+ "${PN}-nautilus-extension"
PACKAGES =+ "${PN}-browser-plugin"

FILES:${PN} += "${datadir}/dbus-1 \
                ${datadir}/metainfo \
                ${datadir}/thumbnailers \
"
FILES:${PN}-nautilus-extension = "${libdir}/nautilus/*/*so"
FILES:${PN}-browser-plugin = "${libdir}/mozilla/*/*so"
