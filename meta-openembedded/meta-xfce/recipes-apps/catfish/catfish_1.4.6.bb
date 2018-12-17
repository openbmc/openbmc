SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app distutils3 gtk-icon-cache

DEPENDS += "python3-distutils-extra-native"

B = "${S}"

SRC_URI[md5sum] = "879197c9400be2a80d51abf494bfffb6"
SRC_URI[sha256sum] = "e1caee14c5268ac781434701c0eb26eddfced2c0f1ae066549583ed05f99adbf"

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject"
