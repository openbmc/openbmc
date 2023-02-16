SUMMARY = "Xfce4 Desktop Manager"
SECTION = "x11/base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = " \
    cairo \
    exo \
    garcon \
    glib-2.0 \
    gtk+3 \
    intltool \
    libwnck3 \
    libxfce4ui \
    libxfce4util \
    thunar \
    xfconf \
"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "ef9268190c25877e22a9ff5aa31cc8ede120239cb0dfca080c174e7eed4ff756"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

FILES:${PN} += "${datadir}/backgrounds"
