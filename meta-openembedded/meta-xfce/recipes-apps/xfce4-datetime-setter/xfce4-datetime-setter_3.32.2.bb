DESCRIPTION = "A fork of (early) gnome-control-center datetime panel for XFCE. \
It is based upon GTK3 and embedds into recent xfce4-settings."
HOMEPAGE = "https://github.com/schnitzeltony/xfce4-datetime-setter"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

DEPENDS = "glib-2.0-native libxfce4ui"

SRC_URI = "git://github.com/schnitzeltony/xfce4-datetime-setter.git;protocol=https;branch=master \
           file://fix-inner-dependency.patch \
           file://0001-Fix-build-with-meson-0.61.patch \
"
SRCREV = "5c7a73a3824b03b91719e05e2604b97c7a72d50f"

S = "${WORKDIR}/git"

inherit gettext meson features_check pkgconfig

REQUIRED_DISTRO_FEATURES = "systemd x11"

FILES:${PN} += "${datadir}/icons/hicolor"

RDEPENDS:${PN} = "tzdata"
