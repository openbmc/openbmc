SUMMARY = "GNOME control center"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "desktop-file-utils-native gnome-menus libunique dbus-glib gnome-desktop libxml2 metacity gconf gnome-settings-daemon librsvg pango libgnomekbd libxklavier libcanberra libgtop libxscrnsaver"

PR = "r1"

inherit gnome

SRC_URI[archive.md5sum] = "b4e8ab5c7556ae07addbfcfb4fa2f761"
SRC_URI[archive.sha256sum] = "7c568b57358e5c08f4d8dd76dbac7df2539135ad081872b60514b7a8ac797e66"
GNOME_COMPRESS_TYPE="bz2"

LDFLAGS += "-lgthread-2.0 -lxml2"

do_configure_prepend() {
    sed -i s:help::g ${S}/Makefile.am
}
do_install_append() {
    rm -rf ${D}${datadir}/mime
}

FILES_${PN} += "${datadir}/icon* \
                ${datadir}/xsession* \
                ${libdir}/window-manager-settings/*.so \
                ${datadir}/gnome \
                ${datadir}/desktop-directories \
"
FILES_${PN}-dbg += "${libdir}/window-manager-settings/.debug"
FILES_${PN}-dev += "${libdir}/window-manager-settings/*.la"
FILES_${PN}-staticdev += "${libdir}/window-manager-settings/*.a"
