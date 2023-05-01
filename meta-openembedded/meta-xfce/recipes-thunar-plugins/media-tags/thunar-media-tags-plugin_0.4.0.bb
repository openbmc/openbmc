SUMMARY = "Adds ID3/OGG tag support to the Thunar bulk rename dialog"
HOMEPAGE = "https://docs.xfce.org/xfce/thunar/media-tags"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit thunar-plugin

DEPENDS += "taglib"

SRC_URI[sha256sum] = "9592111e11699e449c1fbd5f72d1633ef39b00f17a988cd251ba228dab4fc90a"
