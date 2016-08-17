SUMMARY = "Simple client plugin for Music Player Daemon"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mpc-plugin"
SECTION = "x11/application"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3604d987e6dfdfc672c754d08953b0e0"

inherit xfce-panel-plugin

DEPENDS += "libmpd"

# for now we recommend our own mpd-server
RRECOMMENDS_${PN} = "mpd"

SRC_URI[md5sum] = "718e64748e46908a44cd0b96eacbda28"
SRC_URI[sha256sum] = "e71f57a28915c57459d6ce0eeeee1d0934f523c0ed083158c3d3b3836fc06fcf"
