SUMMARY = "Configuration editor for dconf"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = "dconf gtk+3"

inherit gnomebase vala gettext gsettings bash-completion

SRC_URI[archive.md5sum] = "2907205ad1a0e2774c981932cb7ff25b"
SRC_URI[archive.sha256sum] = "cecc2a5cb44af68be80e970e83fb9e3e92e2a74df7c90b63324e6da19a929d5f"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
