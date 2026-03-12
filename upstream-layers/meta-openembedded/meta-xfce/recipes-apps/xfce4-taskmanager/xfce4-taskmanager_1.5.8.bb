SUMMARY = "Easy to use task manager"
HOMEPAGE = "https://docs.xfce.org/apps/xfce4-taskmanager/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxfce4ui libxmu xfce4-dev-tools-native"

SRC_URI[sha256sum] = "14b9d68b8feb88a642a9885b8549efe7fc9e6c155f638003f2a4a58d9eb2baab"
