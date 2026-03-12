DESCRIPTION = "This plugin cyclically spawns the indicated script/program, captures its output (stdout) and displays the resulting string into the panel."
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-genmon-plugin/start"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

SRC_URI[sha256sum] = "077197911d84e5ba22e7bb895ce6c038dbbd8e8e0067ed6f4e48502b7167a282"

FILES:${PN} += "${datadir}/xfce4/genmon"
