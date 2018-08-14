SUMMARY = "Adds ID3/OGG tag support to the Thunar bulk rename dialog"
HOMEPAGE = "http://thunar.xfce.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit thunar-plugin

DEPENDS += "taglib"

SRC_URI[md5sum] = "0106e900714f86ccbafdc72238d3cf8d"
SRC_URI[sha256sum] = "056d012a10403ee3e2e55b6ff2faecb796821de9ebee000315589b95d95ed253"
