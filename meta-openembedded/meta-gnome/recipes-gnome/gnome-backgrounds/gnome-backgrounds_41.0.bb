SUMMARY = "GNOME wallpapers"
LICENSE = "GPL-2.0-only & CC-BY-2.0 & CC-BY-SA-2.0 & CC-BY-SA-3.0"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=75859989545e37968a99b631ef42722e \
    file://COPYING_CCBY2;md5=effd72660912b727dfa9722cb295d7be \
    file://COPYING_CCBYSA2;md5=4737b7833b3212fdf30257f056ef3e64 \
    file://COPYING_CCBYSA3;md5=b52fb0a6df395efb7047cb6fc56bfd7e \
"

SECTION = "x11/gnome"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext allarch

SRC_URI[archive.sha256sum] = "1da1ac0d261bedf0fcd2c85b480bc65505e23cf51f1143126c0d37717e693145"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"
