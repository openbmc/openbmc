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

SRC_URI[sha256sum] = "e3befc3e73d2315074eb88933f2b042c5b417f4f7f24be9bd4f4508a091037b7"

FILES:${PN} += "${datadir}/metainfo"
