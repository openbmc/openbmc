SUMMARY = "Clipman is a clipboard manager for Xfce"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-clipman-plugin"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit xfce-panel-plugin

DEPENDS += "xfconf xproto libxtst"

SRC_URI[md5sum] = "f7f2440647493243cbd7787eaee92fcb"
SRC_URI[sha256sum] = "23043f99c3e5257d1f3d68b5ee5125e3469c15620d098e22c6250386197f48a5"

PACKAGECONFIG ??= ""
PACKAGECONFIG[unique] = "--enable-unique,--disable-unique,libunique"
PACKAGECONFIG[qrencode] = "--enable-libqrencode,--disable-libqrencode,qrencode"

FILES_${PN} += "${datadir}/appdata"
