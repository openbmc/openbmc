SUMMARY = "Simple client plugin for Music Player Daemon"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-mpc-plugin/start"
SECTION = "x11/application"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3604d987e6dfdfc672c754d08953b0e0"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin

DEPENDS += "libmpd"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS:${PN} = "mpd"

SRC_URI[sha256sum] = "dee5bcc0566ba2dc95b9c3b4cadd5e8b3bb2798a54a2a8d8652708915fe45d50"
