SUMMARY = "GNOME wallpapers"
LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52fb0a6df395efb7047cb6fc56bfd7e"

SECTION = "x11/gnome"

inherit gnomebase gettext allarch

SRC_URI[archive.sha256sum] = "4ddd3ac439a4a067876805921bb75f4d3c8b85a218d47c276dddde8928443c2e"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"

RDEPENDS:${PN} += "libjxl"
