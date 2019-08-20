SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app distutils3 gtk-icon-cache

DEPENDS += "python3-distutils-extra-native"

B = "${S}"

SRC_URI[md5sum] = "829824fba33e86d03345bbc718e7b2d9"
SRC_URI[sha256sum] = "29a39b85804336e4819dceff203693a5904b7e11d7e024b49b5aab2649ed944e"

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject"
