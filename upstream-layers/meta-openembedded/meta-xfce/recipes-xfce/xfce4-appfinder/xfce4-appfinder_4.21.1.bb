DESCRIPTION = "Xfce4 Application Finder"
HOMEPAGE = "https://docs.xfce.org/xfce/xfce4-appfinder/start"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 libxfce4util libxfce4ui garcon xfconf"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "62fba13ec05066e73200f5a34c2fa1e7a1a7c4dbefffbaf29af9e9e97bb4a7c9"

FILES:${PN} += "${datadir}/metainfo"
