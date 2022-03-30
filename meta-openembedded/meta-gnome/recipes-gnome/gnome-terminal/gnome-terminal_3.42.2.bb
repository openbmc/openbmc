SUMMARY = "GNOME terminal"
LICENSE = "GPL-3.0-only & GFDL-1.3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
    file://COPYING.GFDL;md5=a22d0be1ce2284b67950a4d1673dd1b0 \
"

GNOMEBASEBUILDCLASS = "meson"

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

SRC_URI[archive.sha256sum] = "8a9c8e5ef7a3a73b246a947e1190bb08ec98935af860cf0b3aa2fbf4606817a0"
SRC_URI += "file://0001-Add-W_EXITCODE-macro-for-non-glibc-systems.patch"
SRC_URI += "  file://0001-build-Fix-for-newer-meson.patch"

EXTRA_OEMESON += " \
    -Dsearch_provider=false \
    -Dnautilus_extension=false \
"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
    ${systemd_user_unitdir} \
"

RRECOMMENDS:${PN} += "vte-prompt gsettings-desktop-schemas"
