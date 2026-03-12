SUMMARY = "Verve panel plugin is a comfortable command line plugin for the Xfce panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-verve-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "237e0da802cdc02e0ec0c3cdefecb6fa2992ade9f59ce2999779cc30d59c9f24"
DEPENDS += "libpcre"
