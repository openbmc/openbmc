SUMMARY = "Clipman is a clipboard manager for Xfce"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-clipman-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit xfce-panel-plugin

DEPENDS += "xfconf xorgproto libxtst"

SRC_URI[md5sum] = "c9306940c0ca7abd838a14c9d19b3a7e"
SRC_URI[sha256sum] = "b3e8317796289196fa78c4526626fe061fd35d11e848cf300ca1ace47febceb4"

PACKAGECONFIG ??= ""
PACKAGECONFIG[qrencode] = "--enable-libqrencode,--disable-libqrencode,qrencode"

FILES_${PN} += "${datadir}/appdata"
