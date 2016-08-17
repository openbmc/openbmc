SUMMARY = "Music Player Daemon (MPD) client written in GTK+"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfmpc"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "gtk+ libxfce4util libxfce4ui libmpd vala-native"

# for now we recommend our own mpd-server
RRECOMMENDS_${PN} = "mpd"

inherit xfce-app

SRC_URI[md5sum] = "e4e198850c2467c47783969ac9c16ec0"
SRC_URI[sha256sum] = "ed0cc2940bd5928bb30fab1531c22185a97bd0cc5beacd9e3be4d4cd994a6862"
