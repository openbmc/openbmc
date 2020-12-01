SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app distutils3 gtk-icon-cache mime-xdg

DEPENDS += "python3-distutils-extra-native"

B = "${S}"

SRC_URI[md5sum] = "750b65401c9445e185e71c808d7509a4"
SRC_URI[sha256sum] = "58c0ea06e5f286019295545fbfd1dbca23aea74c625762bbb4c89a6f484ae839"

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject"
