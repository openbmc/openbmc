DESCRIPTION = "Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 libxfce4util libxfce4ui garcon xfconf"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "9ea2c1c2e5630d0670c9cd47793144ee63c04a63b8b68f0397432e043cd62d27"

FILES:${PN} += "${datadir}/metainfo"
