SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app distutils3 gtk-icon-cache mime-xdg

DEPENDS += "python3-distutils-extra-native"

SRC_URI[sha256sum] = "1f6facee57a659af560f06024ca6f98aa4d638bf57a8bcfb613b4dc70fcc3b47"

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject"
