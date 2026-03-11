SUMMARY = "Lightweight image viewer for X"
DESCRIPTION = "GPicView is a simple, lightweight and fast GTK+ based image \
viewer with minimal lib dependency and desktop independent."
HOMEPAGE = "https://github.com/lxde/gpicview"
BUGTRACKER = "https://github.com/lxde/gpicview/issues"
SECTION = "x11"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "glib-2.0-native intltool-native jpeg libx11"

SRC_URI = "git://github.com/lxde/gpicview.git;protocol=https;branch=master;tag=${PV}"
SRCREV = "ca13623c6176585db4759ce4371fbf89c56fa630"

inherit autotools mime-xdg pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ?= "${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'gtk3', 'gtk2', d)}"
PACKAGECONFIG[gtk2] = ",,gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3,,gtk+3"

FILES:${PN} += "${datadir}/icons"
