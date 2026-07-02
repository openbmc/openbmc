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

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/49/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive"
SRC_URI[archive.sha256sum] = "90a8ccfadf51dff31e0028324fb9a358b2d26c5ae861a71c7dbf9f4dd9bdd399"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
