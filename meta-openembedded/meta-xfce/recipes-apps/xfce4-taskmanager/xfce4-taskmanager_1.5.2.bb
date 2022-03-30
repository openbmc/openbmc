SUMMARY = "Easy to use task manager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxfce4ui libxmu xfce4-dev-tools-native"

SRC_URI[sha256sum] = "bd25143f47a29000b4148874863dffa521b1a37cb01dbc026f423ea3160f9a35"
