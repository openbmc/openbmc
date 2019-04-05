SUMMARY = "Configuration editor for dconf"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "dconf gtk+3"

inherit gnomebase vala gettext gsettings bash-completion

SRC_URI[archive.md5sum] = "331a3603c0f8a9913e3a3c0f178b5310"
SRC_URI[archive.sha256sum] = "f19d1332ac27e23ef3dc2ed07ba4e4646d9d7f05e2e78748aa525a1320adbaba"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
