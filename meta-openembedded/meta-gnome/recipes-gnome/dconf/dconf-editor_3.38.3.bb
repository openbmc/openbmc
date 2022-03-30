SUMMARY = "Configuration editor for dconf"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "dconf gtk+3"

inherit gnomebase vala gettext gsettings bash-completion

SRC_URI += " file://0001-editor-meson.build-fix-meson-0.61-errors.patch"
SRC_URI[archive.sha256sum] = "571af4c7dad4f049b53e6cd728b79addf08c27ddab6bc57b396d211866ee79e3"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
