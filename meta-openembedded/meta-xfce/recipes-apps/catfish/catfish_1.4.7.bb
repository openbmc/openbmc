SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app distutils3 gtk-icon-cache

DEPENDS += "python3-distutils-extra-native"

B = "${S}"

SRC_URI[md5sum] = "f4c1c2b2c560824cab8d7f6c7bd624ca"
SRC_URI[sha256sum] = "48caaac872d29e2762e31e59ade1310cb860af55f1a36f2520ce1d90c39227e9"

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject"
