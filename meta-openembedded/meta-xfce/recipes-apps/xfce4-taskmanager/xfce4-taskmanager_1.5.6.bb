SUMMARY = "Easy to use task manager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxfce4ui libxmu xfce4-dev-tools-native"

SRC_URI[sha256sum] = "20979000761a41faed4f7f63f27bd18bb36fb27db4f7ecc8784a460701fb4abb"
