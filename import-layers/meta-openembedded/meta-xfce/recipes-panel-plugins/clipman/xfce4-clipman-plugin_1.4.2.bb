SUMMARY = "Clipman is a clipboard manager for Xfce"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-clipman-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit xfce-panel-plugin

DEPENDS += "xfconf xproto libxtst"

SRC_URI[md5sum] = "9169140af7c9d3fddd8a5c9e0efb5a61"
SRC_URI[sha256sum] = "cf2b45e327e67cc187dcb99a6ef6d95570463045accd24540e17172f005189b3"

PACKAGECONFIG ??= ""
PACKAGECONFIG[qrencode] = "--enable-libqrencode,--disable-libqrencode,qrencode"

FILES_${PN} += "${datadir}/appdata"
