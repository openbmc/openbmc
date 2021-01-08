DESCRIPTION = "Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "glib-2.0 gtk+3 libxfce4util libxfce4ui garcon xfconf"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "37b92aaaeeec8220ed23163cf89321168d3b49e0c48b4c10f12dc4a21fdf0954"

FILES_${PN} += "${datadir}/metainfo"
