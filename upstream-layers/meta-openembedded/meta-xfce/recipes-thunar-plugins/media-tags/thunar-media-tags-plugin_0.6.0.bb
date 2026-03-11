SUMMARY = "Adds ID3/OGG tag support to the Thunar bulk rename dialog"
HOMEPAGE = "https://docs.xfce.org/xfce/thunar/media-tags"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit thunar-plugin

DEPENDS += "taglib"

SRC_URI[sha256sum] = "b62dc047100346324e63d46acaaa497e8d7fccd1d10ef5bfb8370fd666a48c4a"
