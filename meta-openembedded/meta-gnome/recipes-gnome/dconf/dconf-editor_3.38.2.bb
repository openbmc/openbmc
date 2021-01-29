SUMMARY = "Configuration editor for dconf"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "dconf gtk+3"

inherit gnomebase vala gettext gsettings bash-completion

SRC_URI[archive.sha256sum] = "1253dad87e6213fbf313ff9ec9dc4358aa1b10261f28072c1dc0e0997b92f835"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
