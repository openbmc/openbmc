SUMMARY = "Easy to use task manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-app

DEPENDS += "gtk+ cairo libwnck libxmu xfce4-dev-tools-native exo-native"

SRC_URI[md5sum] = "4a2d0b1e8001fb343139a97d57b56eaa"
SRC_URI[sha256sum] = "22e523e2ee231713f40a48890d8cbae99320ac1173f7c68502f490318e1e0409"
