SUMMARY = "Xfce4 Desktop Manager"
HOMEPAGE = "https://docs.xfce.org/xfce/xfdesktop/start"
SECTION = "x11/base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = " \
    cairo \
    exo \
    garcon \
    glib-2.0 \
    gtk+3 \
    libwnck3 \
    libxfce4ui \
    libxfce4util \
    libxfce4windowing \
    libyaml \
    thunar \
    xfconf \
"

XFCEBASEBUILDCLASS = "meson"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "1d9bd76015fb6e9aca05e73cd998c7c66ed4fc8c10b626e08fc2eb7c39df3f7b"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "-Dnotifications=enabled,-Dnotifications=disabled,libnotify"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,"

FILES:${PN} += "${datadir}/backgrounds"
