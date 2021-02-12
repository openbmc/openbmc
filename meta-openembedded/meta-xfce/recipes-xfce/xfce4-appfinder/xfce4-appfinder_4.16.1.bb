DESCRIPTION = "Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "glib-2.0 gtk+3 libxfce4util libxfce4ui garcon xfconf"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "bfe3e9bd92695014ee74a2fbb7f5fd1b4c29cf043c4a11598b8958324c81e7ec"

FILES_${PN} += "${datadir}/metainfo"
