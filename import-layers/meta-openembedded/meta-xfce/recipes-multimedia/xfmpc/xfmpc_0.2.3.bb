SUMMARY = "Music Player Daemon (MPD) client written in GTK+"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfmpc"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "gtk+ libxfce4util libxfce4ui libmpd vala-native"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS_${PN} = "mpd"

inherit xfce-app

SRC_URI[md5sum] = "e2d2faeb7a6f62813e287f3d12522b71"
SRC_URI[sha256sum] = "4189c0c82b66b758a6d5bc651493b675d3d46329e5f1a76ff26c448125f4fcb0"
