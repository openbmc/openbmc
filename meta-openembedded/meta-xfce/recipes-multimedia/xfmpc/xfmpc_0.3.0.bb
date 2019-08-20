SUMMARY = "Music Player Daemon (MPD) client written in GTK+"
HOMEPAGE = "https://goodies.xfce.org/projects/applications/xfmpc"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4util libxfce4ui libmpd vala-native"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS_${PN} = "mpd"

inherit xfce-app

SRC_URI[md5sum] = "e6ff8563f159d1e727d656fb88964998"
SRC_URI[sha256sum] = "c76e2a88dc3e1d345da7a5c68fa39981494c2b40033748efcac54411c9e65689"
