DESCRIPTION = "Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 libxfce4util libxfce4ui garcon xfconf"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "d8bd2a1dd9d193eb25e8e5e68b1beae3578a46d1c5eeee30bec7dabbf4330648"

FILES:${PN} += "${datadir}/metainfo"
