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

inherit gnomebase itstool gnome-help pkgconfig gsettings gobject-introspection upstream-version-is-even gettext features_check

SRC_URI[archive.md5sum] = "77b38b625008f5940e9bd4b3d38e8cf8"
SRC_URI[archive.sha256sum] = "85e407cde396147b249120d816c7715fb6df191ca561cf65ff3554a5d67626df"
SRC_URI += " \
    file://0001-Add-format-attribute-to-_synctex_malloc.patch \
    file://0002-add-a-formatting-attribute-check.patch \
"

EXTRA_OECONF = " \
    --enable-thumbnailer \
    --without-systemduserunitdir \
"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/libdocument/.libs"
}

PACKAGECONFIG ??= ""
PACKAGECONFIG[nautilus] = "--enable-nautilus,--disable-nautilus,nautilus"
PACKAGECONFIG[browser-plugin] = "--enable-browser-plugin,--disable-browser-plugin,"

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
