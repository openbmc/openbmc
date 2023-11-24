SUMMARY = "GNOME wallpapers"
LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52fb0a6df395efb7047cb6fc56bfd7e"

SECTION = "x11/gnome"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext allarch

SRC_URI[archive.sha256sum] = "cee0e688fbae5ef7a75f335ada2d10779e08ebca9445f1586de32c5a9b6dee2d"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"
