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
    ${@bb.utils.contains('DISTRO_FEATURES','x11','gnome-desktop3','',d)} \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase itstool gnome-help pkgconfig gsettings gobject-introspection upstream-version-is-even gettext mime-xdg gtk-doc

SRC_URI[archive.md5sum] = "9762dabdde37a804072bccbfd311d357"
SRC_URI[archive.sha256sum] = "af2ebdf7f74e6580c4f1a12bdfe26b9ff90374d7acae061de0666d64012a9db2"
SRC_URI += " \
    file://0001-Add-format-attribute-to-_synctex_malloc.patch \
    file://0002-add-a-formatting-attribute-check.patch \
"

GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = " \
    -Dsystemduserunitdir=no \
"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/libdocument/.libs"
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "-Dnautilus=true,-Dnautilus=false,nautilus"
PACKAGECONFIG[browser-plugin] = "-Dbrowser_plugin=true,-Dbrowser_plugin=false"

RDEPENDS_${PN} += "glib-2.0-utils"
RRECOMMMENDS_${PN} = "adwaita-icon-theme"

PACKAGES =+ "${PN}-nautilus-extension"
PACKAGES =+ "${PN}-browser-plugin"

FILES_${PN} += "${datadir}/dbus-1 \
                ${datadir}/metainfo \
                ${datadir}/thumbnailers \
"
FILES_${PN}-nautilus-extension = "${libdir}/nautilus/*/*so"
FILES_${PN}-browser-plugin = "${libdir}/mozilla/*/*so"
