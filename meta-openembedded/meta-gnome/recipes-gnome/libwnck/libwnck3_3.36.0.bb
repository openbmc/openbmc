SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

BPN = "libwnck"

SECTION = "x11/libs"
DEPENDS = "intltool-native gnome-common-native gtk+3 gdk-pixbuf-native libxres"

PACKAGECONFIG ??= "startup-notification"
PACKAGECONFIG[startup-notification] = "-Dstartup_notification=enabled,-Dstartup_notification=disabled,startup-notification"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

inherit gnomebase gobject-introspection gtk-doc gettext features_check

SRC_URI[archive.md5sum] = "00bb40dc6fab76af0da33e88a34b6378"
SRC_URI[archive.sha256sum] = "bc508150b3ed5d22354b0e6774ad4eee465381ebc0ace45eb0e2d3a4186c925f"

# libxres means x11 only
REQUIRED_DISTRO_FEATURES = "x11"
