SUMMARY = "GNOME wallpapers"
LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52fb0a6df395efb7047cb6fc56bfd7e"

SECTION = "x11/gnome"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext allarch

SRC_URI[archive.sha256sum] = "19f749d8c37f872f155bb69a2bbe7aca381175e6929f1998047d775cbc637244"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"
