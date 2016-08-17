SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "libwnck"

SECTION = "x11/libs"
DEPENDS = "gtk+3 gdk-pixbuf-native libxres"

PACKAGECONFIG ??= "startup-notification"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

inherit gnomebase gobject-introspection
SRC_URI[archive.md5sum] = "d96c0b74c4dc5fdae758964098603c90"
SRC_URI[archive.sha256sum] = "bb643c9c423c8aa79c59973ce27ce91d3b180d1e9907902278fb79391f52befa"

inherit distro_features_check
# libxres means x11 only
REQUIRED_DISTRO_FEATURES = "x11"

