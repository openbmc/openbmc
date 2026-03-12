SUMMARY = "Easily themable notification daemon with transparency effects"
HOMEPAGE = "https://docs.xfce.org/apps/notifyd/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    glib-2.0-native \
    libnotify \
    libxfce4util \
    libxfce4ui \
    xfconf \
    xfce4-panel \
    python3-packaging-native \
"

inherit xfce-app
inherit python3native

SRC_URI[sha256sum] = "ae6c128c055c44bd07202f73ae69ad833c5e4754f3530696965136e4d9ea7818"

# Avoid trouble with other desktops e.g KDE which also ships dbus service named
# org.freedesktop.Notifications
EXTRA_OECONF = "--disable-dbus-start-daemon \
                GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen \
                GLIB_COMPILE_RESOURCES=${STAGING_BINDIR_NATIVE}/glib-compile-resources \
                GLIB_GENMARSHAL=${STAGING_BINDIR_NATIVE}/glib-genmarshal \
                GLIB_MKENUMS=${STAGING_BINDIR_NATIVE}/glib-mkenums \
               " 

do_compile:prepend() {
    mkdir -p xfce4-notifyd xfce4-notifyd-config
}

FILES:${PN} += " \
    ${systemd_user_unitdir} \
    ${datadir}/xfce4 \
    ${datadir}/themes \
    ${datadir}/dbus-1 \
    ${libdir}/xfce4 \
"
