SUMMARY = "Verve panel plugin is a comfortable command line plugin for the Xfce panel"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-verve-plugin"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "e1bf121f1bf9cf2a199bf5c0f3aa802f503df9bea50724741e7a92fe6d9fe09e"
DEPENDS += "libpcre"
