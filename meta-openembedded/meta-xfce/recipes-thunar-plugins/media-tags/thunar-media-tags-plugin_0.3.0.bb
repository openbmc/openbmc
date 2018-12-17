SUMMARY = "Adds ID3/OGG tag support to the Thunar bulk rename dialog"
HOMEPAGE = "http://thunar.xfce.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit thunar-plugin

DEPENDS += "taglib"

SRC_URI[md5sum] = "5e332113e4b0e548ee7abd87629667f7"
SRC_URI[sha256sum] = "e265c4415abac40337cc5566c6f706efcf0be4e97723abe22ba8b874c93a591b"
