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

SRC_URI[sha256sum] = "4a58bd9e9c14c391943bb35b57ed8eb49000655da176639e7245da7286b07825"

# Fixes build with GCC-14 which enables incompatible-pointer-types as error
CFLAGS += "-Wno-error=incompatible-pointer-types"
