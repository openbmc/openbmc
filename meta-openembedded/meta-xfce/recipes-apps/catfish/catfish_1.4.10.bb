SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app distutils3 gtk-icon-cache

DEPENDS += "python3-distutils-extra-native"

B = "${S}"

SRC_URI[md5sum] = "1b8e7bfd955364b7912fb0a248329b3a"
SRC_URI[sha256sum] = "2573a004105031f871c92fed22a0c4b15bb96f2dff6e36c4f2959f56b62e343d"

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject"
