SUMMARY = "Simple client plugin for Music Player Daemon"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-mpc-plugin"
SECTION = "x11/application"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3604d987e6dfdfc672c754d08953b0e0"

inherit xfce-panel-plugin

DEPENDS += "libmpd"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS:${PN} = "mpd"

SRC_URI[sha256sum] = "0467fb4d1acd982d3c3e0b89cb41019946850524ff19ed0f658a8d56c7b7664d"
