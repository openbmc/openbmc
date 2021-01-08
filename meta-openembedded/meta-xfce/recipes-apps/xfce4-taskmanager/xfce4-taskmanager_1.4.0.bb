SUMMARY = "Easy to use task manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxmu xfce4-dev-tools-native"

SRC_URI[sha256sum] = "655684ddfc15fc09071f78f9fce5439d20bbd2d80ecfc5ba0a08fb38313e7d43"
