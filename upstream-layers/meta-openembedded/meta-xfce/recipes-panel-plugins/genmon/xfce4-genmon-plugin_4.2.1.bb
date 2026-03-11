DESCRIPTION = "This plugin cyclically spawns the indicated script/program, captures its output (stdout) and displays the resulting string into the panel."
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-genmon-plugin/start"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "de540562e1ea58f35a9c815e20736d26af541a0a9372011148cb75b5f0b65951"

FILES:${PN} += "${datadir}/xfce4/genmon"
