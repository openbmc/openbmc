SUMMARY = "GNOME wallpapers"
LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52fb0a6df395efb7047cb6fc56bfd7e"

SECTION = "x11/gnome"

inherit gnomebase gettext allarch

SRC_URI[archive.sha256sum] = "2d6baa011ee97804c7561f7e1cbd8d4763e30b55b8818dda78f9f75afb8d8d05"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"

RDEPENDS:${PN} += "libjxl"
