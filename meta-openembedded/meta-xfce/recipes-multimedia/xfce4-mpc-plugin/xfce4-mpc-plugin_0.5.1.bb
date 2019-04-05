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

SRC_URI[md5sum] = "305dfdacb2b2198e1f1673d61f86e4f5"
SRC_URI[sha256sum] = "635e678c9729663e9eaadfcf58426f7cea37e6d3fda5e818955fbc8ade00de01"
