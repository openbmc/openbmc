SUMMARY = "Easy to use task manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxmu xfce4-dev-tools-native exo-native"

SRC_URI[md5sum] = "202928c8ff0678a9df949d22a43f1614"
SRC_URI[sha256sum] = "dcfc44da7c1f61d03e2c4bd1cdc8f92ce0d48b013bd0d140b3745d79b75dc3c4"
