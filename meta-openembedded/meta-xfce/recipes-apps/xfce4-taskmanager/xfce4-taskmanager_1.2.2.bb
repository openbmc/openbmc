SUMMARY = "Easy to use task manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxmu xfce4-dev-tools-native exo-native"

SRC_URI[md5sum] = "6578625ffbe069b138533ff5e77df734"
SRC_URI[sha256sum] = "e49a61c819a4fd9286a65ae61605984f327c8b26cf939289f644e656bfa20e13"
