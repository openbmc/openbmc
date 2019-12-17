DESCRIPTION = "Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "glib-2.0 gtk+3 libxfce4util libxfce4ui garcon xfconf"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "03b226a2ee20f95243e74d1868e94e0e"
SRC_URI[sha256sum] = "7ec175d4954fceb2e76cbfbca76ac4a4f53fe799d097a14117e7de68e88a4d98"

FILES_${PN} += "${datadir}/metainfo"
