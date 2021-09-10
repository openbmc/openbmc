SUMMARY = "GNOME terminal"
LICENSE = "GPLv3 & GFDL-1.3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
    file://COPYING.GFDL;md5=a22d0be1ce2284b67950a4d1673dd1b0 \
"

inherit gnomebase gsettings gnome-help gettext itstool upstream-version-is-even

DEPENDS = " \
    glib-2.0-native \
    intltool-native \
    yelp-tools-native \
    docbook-xsl-stylesheets-native libxslt-native \
    desktop-file-utils-native \
    gtk+3 \
    gsettings-desktop-schemas \
    vte \
    dconf \
    libpcre2 \
"

SRC_URI[archive.sha256sum] = "4c79af8ffe8bd3a5daca1911ea2c0acd6872860a1dd5d7eea219f4ab4ae556ac"
SRC_URI += "file://0001-Add-W_EXITCODE-macro-for-non-glibc-systems.patch"

EXTRA_OECONF += " \
    --disable-search-provider \
    --without-nautilus-extension \
"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
    ${systemd_user_unitdir} \
"

RRECOMMENDS:${PN} += "vte-prompt gsettings-desktop-schemas"
