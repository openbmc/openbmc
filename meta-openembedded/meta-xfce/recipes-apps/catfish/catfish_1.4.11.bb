SUMMARY = "Catfish is a handy file searching tool for linux and unix"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

inherit xfce-app distutils3 gtk-icon-cache

DEPENDS += "python3-distutils-extra-native"

B = "${S}"

SRC_URI[md5sum] = "67e23d45fded026ef3445bc7fe1d1653"
SRC_URI[sha256sum] = "617baf9309e3cdfb20c8357ac786eb26f30e6fd4280d4534d3cdd742c7ffcd85"

FILES_${PN} += "${datadir}/metainfo"

RDEPENDS_${PN} += "python3-pygobject"
