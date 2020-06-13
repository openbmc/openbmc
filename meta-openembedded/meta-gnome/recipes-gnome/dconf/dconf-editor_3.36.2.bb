SUMMARY = "Configuration editor for dconf"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "dconf gtk+3"

inherit gnomebase vala gettext gsettings bash-completion

SRC_URI[archive.md5sum] = "78bd905ed3c770a00c850d8cffec88a4"
SRC_URI[archive.sha256sum] = "edcec8867f018589125f177407760c642bbbb52fe5122daac5905223d6b3e1c7"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
