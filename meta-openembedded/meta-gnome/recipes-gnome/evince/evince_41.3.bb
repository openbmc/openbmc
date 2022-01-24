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
    ${@bb.utils.contains('DISTRO_FEATURES','x11','gnome-desktop','',d)} \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase itstool gnome-help pkgconfig gsettings gobject-introspection gettext mime-xdg gtk-doc

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI += " file://0001-Remove-incorrect-args-for-i18n.merge_file.patch"
SRC_URI[archive.sha256sum] = "3346b01f9bdc8f2d5ffea92f110a090c64a3624942b5b543aad4592a9de33bb0"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = " \
    -Dsystemduserunitdir=no \
"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/libdocument/.libs"
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "-Dnautilus=true,-Dnautilus=false,nautilus"

RDEPENDS:${PN} += "glib-2.0-utils"
RRECOMMMENDS_${PN} = "adwaita-icon-theme"

PACKAGES =+ "${PN}-nautilus-extension"

FILES:${PN} += "${datadir}/dbus-1 \
                ${datadir}/metainfo \
                ${datadir}/thumbnailers \
"
FILES:${PN}-nautilus-extension = "${libdir}/nautilus/*/*so"
