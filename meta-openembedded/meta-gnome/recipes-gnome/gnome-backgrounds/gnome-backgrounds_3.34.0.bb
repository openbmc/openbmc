SUMMARY = "GNOME wallpapers"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

SECTION = "x11/gnome"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase allarch gettext upstream-version-is-even allarch

SRC_URI[archive.md5sum] = "f350804df16cdc9ef5306087157cf31b"
SRC_URI[archive.sha256sum] = "c2b7fb6db98c05e205053daaa7f58c7f06ff91b45c4006052af17c578ae7b47f"

FILES_${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"
