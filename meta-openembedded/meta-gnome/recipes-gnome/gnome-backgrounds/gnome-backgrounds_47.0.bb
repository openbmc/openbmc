SUMMARY = "GNOME wallpapers"
LICENSE = "CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b52fb0a6df395efb7047cb6fc56bfd7e"

SECTION = "x11/gnome"

inherit gnomebase gettext allarch

SRC_URI[archive.sha256sum] = "874a4a39c4261736f6a854722833400b612441c4681aa5982d90b15abc9c91fd"

FILES:${PN} += " \
    ${datadir}/backgrounds \
    ${datadir}/gnome-background-properties \
"

RDEPENDS:${PN} += "libjxl"
