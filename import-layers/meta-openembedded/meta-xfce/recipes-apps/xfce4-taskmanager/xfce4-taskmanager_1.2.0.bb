SUMMARY = "Easy to use task manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-app

DEPENDS += "gtk+ cairo libwnck libxmu xfce4-dev-tools-native exo-native"

SRC_URI[md5sum] = "6f30ce3c688454812008281065a6e05c"
SRC_URI[sha256sum] = "5746d473ad428b13db7c05cfcbc8099fbea13da6be26d3a9359bcb4de971ba69"
