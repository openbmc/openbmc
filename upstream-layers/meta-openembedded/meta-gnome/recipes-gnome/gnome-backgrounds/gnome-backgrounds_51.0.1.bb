SUMMARY = "GNOME wallpapers"
LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52fb0a6df395efb7047cb6fc56bfd7e"

SECTION = "x11/gnome"

inherit gnomebase gettext allarch

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI[archive.sha256sum] = "ec50534d7749be0e1503c5504f826c61d1460d2b41cb84518fa6b2f5547d1e1e"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"

RDEPENDS:${PN} += "libjxl"
