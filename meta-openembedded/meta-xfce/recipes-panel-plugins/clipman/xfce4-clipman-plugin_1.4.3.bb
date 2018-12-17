SUMMARY = "Clipman is a clipboard manager for Xfce"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-clipman-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit xfce-panel-plugin

DEPENDS += "xfconf xorgproto libxtst"

SRC_URI[md5sum] = "fa0acd5f5e3298e56ebd47d2944cd02b"
SRC_URI[sha256sum] = "29cdb85efb54bd5c9c04cc695b7c4914d6dff972b9fd969cbfb5504e9c632ad2"

PACKAGECONFIG ??= ""
PACKAGECONFIG[qrencode] = "--enable-libqrencode,--disable-libqrencode,qrencode"

FILES_${PN} += "${datadir}/appdata"
