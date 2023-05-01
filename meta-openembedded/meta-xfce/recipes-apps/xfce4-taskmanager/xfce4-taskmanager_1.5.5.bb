SUMMARY = "Easy to use task manager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxfce4ui libxmu xfce4-dev-tools-native"

SRC_URI[sha256sum] = "f64f01ba241a0b8bbf2ed3274e5decc2313c9f8b0e4d160db3ba69b331558ae5"
