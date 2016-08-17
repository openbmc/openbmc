DESCRIPTION = "This plugin cyclically spawns the indicated script/program, captures its output (stdout) and displays the resulting string into the panel."
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-genmon-plugin"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=68ad62c64cc6c620126241fd429e68fe"

inherit xfce-panel-plugin

SRC_URI += "file://0001-Allow-timer-period-to-be-set-at-250ms-resolution.-Pa.patch"

SRC_URI[md5sum] = "24108b339bb040ed360266f53a245224"
SRC_URI[sha256sum] = "b0a5337b49c85623dc89f3c9e47c7374b1d466af2418033d2d6dfc57a9790387"

S = "${WORKDIR}/xfce4-genmon-plugin-3.4"
