SUMMARY = "Configuration editor for dconf"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    dconf \
    gtk+3 \
    glib-2.0 \
    libhandy\
"

inherit gnomebase vala gsettings bash-completion pkgconfig gtk-icon-cache

SRC_URI[archive.sha256sum] = "935a3c2dd76cc2a93cd5aee9a54d3947fb111eb396f4b63dc5f0ba8f8d099136"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
