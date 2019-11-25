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

SRC_URI[archive.md5sum] = "13fa9f5f459481c7f05b6964c470ef16"
SRC_URI[archive.sha256sum] = "3bd723f4058ec014da4715db4181b7d73eccc797b85ad5e6236996951c01803d"
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
