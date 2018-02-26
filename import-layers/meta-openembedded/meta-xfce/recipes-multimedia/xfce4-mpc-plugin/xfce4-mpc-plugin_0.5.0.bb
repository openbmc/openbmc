SUMMARY = "Simple client plugin for Music Player Daemon"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-mpc-plugin"
SECTION = "x11/application"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3604d987e6dfdfc672c754d08953b0e0"

inherit xfce-panel-plugin

DEPENDS += "libmpd"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS_${PN} = "mpd"

SRC_URI[md5sum] = "13d5d95ef0c305bac45fde6231258e5d"
SRC_URI[sha256sum] = "f1320916ae3112e6825699652a502cebfa78bb006c649b42d3d331dfe57b6cb0"
