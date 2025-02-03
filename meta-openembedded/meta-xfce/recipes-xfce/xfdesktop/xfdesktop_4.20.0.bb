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
    libxfce4windowing \
    thunar \
    xfconf \
"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "227041ba80c7f3eb9c99dec817f1132b35d8aec7a4335703f61ba1735cd65632"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

EXTRA_OECONF = "GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen \
                GLIB_COMPILE_RESOURCES=${STAGING_BINDIR_NATIVE}/glib-compile-resources \
                GLIB_GENMARSHAL=${STAGING_BINDIR_NATIVE}/glib-genmarshal \
                GLIB_MKENUMS=${STAGING_BINDIR_NATIVE}/glib-mkenums \
                "

FILES:${PN} += "${datadir}/backgrounds"
