SUMMARY = "Configuration editor for dconf"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "dconf gtk+3"

inherit gnomebase vala gettext gsettings bash-completion

SRC_URI[archive.md5sum] = "cc9eb8020cc848812972d8519c3d05cf"
SRC_URI[archive.sha256sum] = "455b53d827820efd28a176ee52e13eda5cda8cdf4e31e0145cfdd69931bf0c5a"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
