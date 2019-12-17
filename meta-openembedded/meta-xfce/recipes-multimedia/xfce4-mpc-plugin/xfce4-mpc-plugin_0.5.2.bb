SUMMARY = "Simple client plugin for Music Player Daemon"
HOMEPAGE = "https://goodies.xfce.org/projects/panel-plugins/xfce4-mpc-plugin"
SECTION = "x11/application"
LICENSE = "BSD-0-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3604d987e6dfdfc672c754d08953b0e0"

inherit xfce-panel-plugin

DEPENDS += "libmpd"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS_${PN} = "mpd"

SRC_URI[md5sum] = "26a1e8658df2b51967dc2250e23f467d"
SRC_URI[sha256sum] = "eefe78b7b6b95312b3a52814b7f632dc92970c1b3e9535de616315749bf67760"
