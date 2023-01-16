DESCRIPTION = "Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 libxfce4util libxfce4ui garcon xfconf"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "962a98d7b327d2073ed4cd0f78bce7945ed51b97d52fd60196e8b02ef819c18c"

FILES:${PN} += "${datadir}/metainfo"
