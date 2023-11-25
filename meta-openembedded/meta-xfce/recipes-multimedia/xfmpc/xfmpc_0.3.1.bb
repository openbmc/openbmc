SUMMARY = "Music Player Daemon (MPD) client written in GTK+"
HOMEPAGE = "https://goodies.xfce.org/projects/applications/xfmpc"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "libxfce4util libxfce4ui libmpd vala-native"

# While this item does not require it, it depends on mpd which does
LICENSE_FLAGS = "commercial"

# for now we recommend our own mpd-server
RRECOMMENDS:${PN} = "mpd"

inherit xfce-app

SRC_URI[sha256sum] = "4867d5dd100fa42ab39ebde6c784ec21ee31717f1adb3f4da070dafb3848d96d"
