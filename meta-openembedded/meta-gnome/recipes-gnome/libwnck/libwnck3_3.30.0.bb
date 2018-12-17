SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "libwnck"

SECTION = "x11/libs"
DEPENDS = "intltool-native gnome-common-native gtk+3 gdk-pixbuf-native libxres"

PACKAGECONFIG ??= "startup-notification"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

inherit gnomebase gobject-introspection gtk-doc gettext

SRC_URI[archive.md5sum] = "60109c2ab0b07da1099ee57980054de1"
SRC_URI[archive.sha256sum] = "ac6d0d2646aa80676d3066651e73abb7bff7ed79be238c9b21a0083e2adc3439"

inherit distro_features_check
# libxres means x11 only
REQUIRED_DISTRO_FEATURES = "x11"
