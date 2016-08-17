SUMMARY = "Easy to use task manager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit xfce-app

DEPENDS += "gtk+ cairo libwnck xfce4-dev-tools-native exo-native"

SRC_URI[md5sum] = "7da465a4798629ebd8650fef62770ab7"
SRC_URI[sha256sum] = "2e1eb161f966cbfbd68bd029fb59115bc5ab0c0704cb500d20e7d73967e59ecb"
