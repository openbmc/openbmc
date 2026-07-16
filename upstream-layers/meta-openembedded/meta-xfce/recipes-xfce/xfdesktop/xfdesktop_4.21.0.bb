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
XFCE_COMPRESS_TYPE = "xz"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "10a463b9bc474159622d861c1bf7f098e7848af15a453ac0275a3b92a8565c4e"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "-Dnotifications=enabled,-Dnotifications=disabled,libnotify"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,"
PACKAGECONFIG[video-backdrop] = "-Dvideo-backdrop=true,-Dvideo-backdrop=false,gstreamer1.0"

FILES:${PN} += "${datadir}/backgrounds"
