SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "libwnck"

SECTION = "x11/libs"
DEPENDS = "intltool-native gnome-common-native gtk+3 gdk-pixbuf-native libxres"

PACKAGECONFIG ??= "startup-notification"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

inherit gnomebase gobject-introspection gtk-doc
SRC_URI[archive.md5sum] = "487938d65d4bfae1f2501052b1bd7492"
SRC_URI[archive.sha256sum] = "1cb03716bc477058dfdf3ebfa4f534de3b13b1aa067fcd064d0b7813291cba72"

inherit distro_features_check
# libxres means x11 only
REQUIRED_DISTRO_FEATURES = "x11"
