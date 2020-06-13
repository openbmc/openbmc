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
    desktop-file-utils-native \
    gtk+3 \
    gsettings-desktop-schemas \
    vte \
    dconf \
    libpcre2 \
"

SRC_URI[archive.md5sum] = "08150cbf2e23dd4f60f959a6eca8ef0c"
SRC_URI[archive.sha256sum] = "41d1b6a3dc97c066e294acdb7f36931e81668638dcc92ffa25bca3ddebacdf46"
SRC_URI += "file://0001-Add-W_EXITCODE-macro-for-non-glibc-systems.patch"

EXTRA_OECONF += " \
    --disable-search-provider \
    --without-nautilus-extension \
"

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
    ${systemd_user_unitdir} \
"

RRECOMMENDS_${PN} += "vte-prompt gsettings-desktop-schemas"
