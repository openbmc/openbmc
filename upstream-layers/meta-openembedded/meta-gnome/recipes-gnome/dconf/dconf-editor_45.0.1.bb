SUMMARY = "Configuration editor for dconf"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"


DEPENDS = " \
    dconf \
    desktop-file-utils-native \
    gtk+3 \
    glib-2.0 \
    libhandy\
"

inherit gnomebase vala gsettings bash-completion pkgconfig gtk-icon-cache

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/45/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive"
SRC_URI[archive.sha256sum] = "1180297678eedae6217cc514a2638c187d2f1d1ef2720cb9079b740c429941dd"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
