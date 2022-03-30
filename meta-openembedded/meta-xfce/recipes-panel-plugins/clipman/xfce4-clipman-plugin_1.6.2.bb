SUMMARY = "Clipman is a clipboard manager for Xfce"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-clipman-plugin"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit xfce-panel-plugin

DEPENDS += "xfconf xorgproto libxtst"

SRC_URI[sha256sum] = "ab8a5fe6f68fb1789190e498243a1d1385de3f64e984f470cbd3d1eb779399b8"

PACKAGECONFIG ??= ""
PACKAGECONFIG[qrencode] = "--enable-libqrencode,--disable-libqrencode,qrencode"

FILES:${PN} += "${datadir}/metainfo"
