SUMMARY = "Simple client plugin for Music Player Daemon"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-mpc-plugin/start"
SECTION = "x11/application"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3604d987e6dfdfc672c754d08953b0e0"

inherit xfce-panel-plugin

DEPENDS += "libmpd"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS:${PN} = "mpd"

SRC_URI[sha256sum] = "4ce7d77667a263ee9916c0cab2a733b17e3bd65705cd4ed5cec3cbde6e7298cf"
